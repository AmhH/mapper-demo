package com.example.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
  private Map<String, Name> personalNames;
  //private String[] firstNames;
  //private List<String> lastNames;
}
