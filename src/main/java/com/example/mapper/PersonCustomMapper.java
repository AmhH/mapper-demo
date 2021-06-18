package com.example.mapper;

import com.example.model.Person2;
import com.example.model.Personne2;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.SneakyThrows;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

public class PersonCustomMapper extends CustomMapper<Personne2, Person2> {

  @Override
  public void mapAtoB(Personne2 personne2, Person2 person2, MappingContext context) {
    Date date = new Date(personne2.getDtob());
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String isoDate = format.format(date);
    person2.setDtob(isoDate);
  }

  @SneakyThrows
  @Override
  public void mapBtoA(Person2 person2, Personne2 personne2, MappingContext context) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date date = format.parse(person2.getDtob());
    long timestamp = date.getTime();
    personne2.setDtob(timestamp);
  }
}
