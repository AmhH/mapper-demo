package com.example.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.model.Destination;
import com.example.model.Name;
import com.example.model.Person;
import com.example.model.Person2;
import com.example.model.Person3;
import com.example.model.PersonContainer;
import com.example.model.PersonDto;
import com.example.model.PersonNameList;
import com.example.model.PersonNameMap;
import com.example.model.PersonNameParts;
import com.example.model.Personne;
import com.example.model.Personne2;
import com.example.model.Source;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class UsingMapperFactoryTest {

  private MapperFactory mapperFactory;

  CustomMapper<Personne2, Person2> customMapper;

  // constant to help us cover time zone differences
  private final long GMT_DIFFERENCE = 46800000;

  @BeforeEach
  public void before() {
    mapperFactory = new DefaultMapperFactory.Builder().build();
    customMapper = new PersonCustomMapper();
  }

  @Test
  public void givenSrcAndDest_whenMaps_thenCorrect() {
    mapperFactory.classMap(Source.class, Destination.class).byDefault();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Source dest = new Source("Sewyew", 10);
    Destination source = mapper.map(dest, Destination.class);

    assertEquals(source.getName(), dest.getName());
    assertEquals(source.getAge(), dest.getAge());
  }

  @Test
  public void givenSrcAndDest_whenReverseMaps_thenCorrect() {
    mapperFactory.classMap(Source.class, Destination.class).byDefault();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Destination dest = new Destination("Sewyew", 10);
    Source source = mapper.map(dest, Source.class);

    assertEquals(source.getName(), dest.getName());
    assertEquals(source.getAge(), dest.getAge());
  }

  @Test
  public void givenSrcAndDest_whenMapsUsingBoundMapper_thenCorrect() {
    final BoundMapperFacade<Source, Destination> boundMapper = mapperFactory
        .getMapperFacade(Source.class, Destination.class);
    Source source = new Source("Ethiopia", 3000);
    Destination dest = boundMapper.map(source);

    assertEquals(source.getName(), dest.getName());
    assertEquals(source.getAge(), dest.getAge());
  }

  @Test
  public void givenSrcAndDest_whenMapsUsingBoundMapperInReverse_thenCorrect() {
    final BoundMapperFacade<Source, Destination> boundMapper = mapperFactory
        .getMapperFacade(Source.class, Destination.class);
    Destination dest = new Destination("Ethiopia", 3000);
    Source source = boundMapper.mapReverse(dest);

    assertEquals(dest.getName(), source.getName());
    assertEquals(dest.getAge(), source.getAge());
  }

  @Test
  public void givenSrcAndDestWithDifferentFieldNames_whenMaps_thenCorrect() {
    mapperFactory.classMap(Personne.class, Person.class)
        .field("nom", "name")
        .field("surnom", "nickname")
        .field("age", "age").register();
    /*mapperFactory.classMap(Personne.class, Person.class)
        .field("nom", "name")
        .field("surnom", "nickname")
        .byDefault().register();*/

    MapperFacade mapper = mapperFactory.getMapperFacade();
    Personne frenchPerson = new Personne("Claire", "cla", 25);
    Person englishPerson = mapper.map(frenchPerson, Person.class);

    assertEquals(englishPerson.getName(), frenchPerson.getNom());
    assertEquals(englishPerson.getNickname(), frenchPerson.getSurnom());
    assertEquals(englishPerson.getAge(), frenchPerson.getAge());
  }

  @Test
  public void givenSrcAndDest_whenCanExcludeField_thenCorrect() {
    mapperFactory.classMap(Personne.class, Person.class)
        .exclude("nom")
        .field("surnom", "nickname")
        .field("age", "age").register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Personne frenchPerson = new Personne("Claire", "cla", 25);
    Person englishPerson = mapper.map(frenchPerson, Person.class);

    assertEquals(null, englishPerson.getName());
    assertEquals(englishPerson.getNickname(), frenchPerson.getSurnom());
    assertEquals(englishPerson.getAge(), frenchPerson.getAge());
  }

  @Test
  public void givenSrcWithListAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {
    mapperFactory.classMap(PersonNameList.class, PersonNameParts.class)
        .field("nameList[0]", "firstName")
        .field("nameList[1]", "lastName").register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    List<String> nameList = Arrays.asList(new String[]{"Sylvester", "Stallone"});
    PersonNameList src = new PersonNameList(nameList);
    PersonNameParts dest = mapper.map(src, PersonNameParts.class);

    assertEquals(dest.getFirstName(), "Sylvester");
    assertEquals(dest.getLastName(), "Stallone");
  }

  @Test
  public void givenSrcWithMapAndDestWithPrimitiveAttributes_whenMaps_thenCorrect() {
    mapperFactory.classMap(PersonNameMap.class, PersonNameParts.class)
        .field("nameMap['first']", "firstName")
        .field("nameMap[\"last\"]", "lastName")
        .register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Map<String, String> nameMap = new HashMap<>();
    nameMap.put("first", "Leornado");
    nameMap.put("last", "DiCaprio");
    PersonNameMap src = new PersonNameMap(nameMap);
    PersonNameParts dest = mapper.map(src, PersonNameParts.class);

    assertEquals(dest.getFirstName(), "Leornado");
    assertEquals(dest.getLastName(), "DiCaprio");
  }

  @Test
  public void givenSrcWithNestedFields_whenMaps_thenCorrect() {
    mapperFactory.classMap(PersonContainer.class, PersonNameParts.class)
        .field("name.firstName", "firstName")
        .field("name.lastName", "lastName")
        .register();

    MapperFacade mapper = mapperFactory.getMapperFacade();
    PersonContainer src = new PersonContainer(new Name("Nick", "Canon", ""));
    PersonNameParts dest = mapper.map(src, PersonNameParts.class);

    assertEquals(dest.getFirstName(), "Nick");
    assertEquals(dest.getLastName(), "Canon");
  }

  @Test
  public void givenSrcToDestWithNestedFields_whenMaps_thenCorrect() {
    mapperFactory.classMap(PersonNameParts.class, PersonContainer.class)
        .field("firstName", "name.firstName")
        .field("lastName", "name.lastName")
        .register();

    MapperFacade mapper = mapperFactory.getMapperFacade();
    PersonNameParts src = new PersonNameParts("Nick", "Canon");
    PersonContainer dest = mapper.map(src, PersonContainer.class);

    assertEquals(dest.getName().getFirstName(), "Nick");
    assertEquals(dest.getName().getLastName(), "Canon");
  }

  @Test
  public void givenSrcWithNullField_whenMapsThenCorrect() {
    mapperFactory.classMap(Source.class, Destination.class).byDefault();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Source src = new Source(null, 10);
    Destination dest = mapper.map(src, Destination.class);

    assertEquals(dest.getAge(), src.getAge());
    assertEquals(dest.getName(), src.getName());
    assertNull(dest.getName());
    assertNull(src.getName());
  }

  @Test
  public void givenSrcWithNullAndGlobalConfigForNoNull_whenFailsToMap_ThenCorrect() {
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
        .mapNulls(false).build();
    mapperFactory.classMap(Source.class, Destination.class);
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Source src = new Source(null, 10);
    Destination dest = new Destination("Clinton", 55);
    mapper.map(src, dest);

    assertEquals(dest.getAge(), src.getAge());
    assertEquals(dest.getName(), "Clinton");
  }

  @Test
  public void givenSrcWithNullAndLocalConfigForNoNull_whenFailsToMap_ThenCorrect() {
    mapperFactory.classMap(Source.class, Destination.class).field("age", "age")
        .mapNulls(false).field("name", "name").byDefault().register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Source src = new Source(null, 10);
    Destination dest = new Destination("Clinton", 55);
    mapper.map(src, dest);

    assertEquals(dest.getAge(), src.getAge());
    assertEquals(dest.getName(), "Clinton");
  }

  @Test
  public void
  givenDestWithNullReverseMappedToSourceAndLocalConfigForNoNull_whenFailsToMap_thenCorrect() {
    mapperFactory.classMap(Source.class, Destination.class).field("age", "age")
        .mapNullsInReverse(false).field("name", "name").byDefault()
        .register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Destination src = new Destination(null, 10);
    Source dest = new Source("Vin", 44);
    mapper.map(src, dest);

    assertEquals(dest.getAge(), src.getAge());
    assertEquals(dest.getName(), "Vin");
  }

  @Test
  public void givenSrcWithNullAndFieldLevelConfigForNoNull_whenFailsToMap_ThenCorrect() {
    mapperFactory.classMap(Source.class, Destination.class).field("age", "age")
        .fieldMap("name", "name").mapNulls(false).add().byDefault().register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    Source src = new Source(null, 10);
    Destination dest = new Destination("Clinton", 55);
    mapper.map(src, dest);

    assertEquals(dest.getAge(), src.getAge());
    assertEquals(dest.getName(), "Clinton");
  }

  @Test
  public void givenSrcAndDest_whenCustomMapperWorks_thenCorrect() {
    mapperFactory.classMap(Personne2.class, Person2.class)
        .customize(customMapper).register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    String dateTime = "2007-06-26T21:22:39Z";
    long timestamp = new Long("1182882159000");
    Personne2 personne2 = new Personne2("Leornardo", timestamp);
    Person2 person2 = mapper.map(personne2, Person2.class);

    //Time zone difference will make it fail
    //assertEquals(person2.getDtob(), dateTime);
  }

  @Test
  public void givenSrcAndDest_whenCustomMapperWorksBidirectionally_thenCorrect() {
    mapperFactory.classMap(Personne2.class, Person2.class)
        .customize(customMapper).register();
    MapperFacade mapper = mapperFactory.getMapperFacade();
    String dateTime = "2007-06-26T21:22:39Z";
    long timestamp = new Long("1182882159000");
    Person2 person2 = new Person2("Leornardo", dateTime);
    Personne2 personne2 = mapper.map(person2, Personne2.class);

    //Time zone difference will make it fail
    //assertEquals(personne2.getDtob(), timestamp);
  }

  @Test
  @Disabled
  public void msc_methods() {
    mapperFactory.classMap(Source.class, Destination.class)
        .fieldAToB("name", "name")//unidirectional
        .constructorA("name", "id")
        .exclude("id")
        .register();
  }

  @Test
  public void msc_methods1() {
    Set<Set<String>> groups = new HashSet<>();
    groups.add(Collections.singleton("3"));
    groups.add(Collections.singleton("6"));
    groups.add(Collections.singleton("8"));
    final int reduce = groups.stream()
        .map(s -> s.size())
        .reduce(0, (a, b) -> (int) (Math.ceil(Math.sqrt(a)) + Math.ceil(Math.sqrt(b))));
  }

  @Test
  @Disabled
  public void mappingNestedMultiOccurrenceElements() {
    mapperFactory.classMap(Person3.class, PersonDto.class)
        .field("names{fullName}", "personalNames{key}")
        .field("names{}", "personalNames{value}")
        .register();
    Name n1 = new Name("John", "Doe", "John Doe");
    Name n2 = new Name("Jane", "Doe", "Jane Doe");
    Name n3 = new Name("Sam", "Smith", "Sam Smith");
    Name n4 = new Name("Will", "Charles", "Will Charles");
    List<Name> names = new ArrayList<>();
    names.add(n1);
    names.add(n2);
    names.add(n3);
    names.add(n4);
    Person3 person3 = new Person3(names);
    MapperFacade mapper = mapperFactory.getMapperFacade();

    final PersonDto personDto = mapper.map(person3, PersonDto.class);

    assertEquals(4, personDto.getPersonalNames().size());
  }
}