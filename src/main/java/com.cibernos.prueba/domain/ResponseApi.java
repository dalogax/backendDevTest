package com.cibernos.prueba.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseApi {

       public String  method;
       public Integer status;
       public String path;
       public Object body ;
       public String headers ;
       public Boolean isRegexPah;
    }

