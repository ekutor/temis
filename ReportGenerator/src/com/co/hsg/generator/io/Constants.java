 package com.co.hsg.generator.io;
 
 public enum Constants
 {
   PATH_APP("/bbn6/dvc"), 
   LOGO("resources/icono.png");
 
   private String value;
 
   private Constants(String value) { this.value = value; }
 
   public String getValue()
   {
     return this.value;
   }
 }
