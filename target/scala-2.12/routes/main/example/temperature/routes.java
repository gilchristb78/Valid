
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/ben/IdeaProjects/nextgen-solitaire/src/main/resources/routes
// @DATE:Tue Oct 04 13:18:10 EDT 2022

package example.temperature;

import router.RoutesPrefix;

public class routes {
  
  public static final example.temperature.ReverseTemperature Temperature = new example.temperature.ReverseTemperature(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final example.temperature.javascript.ReverseTemperature Temperature = new example.temperature.javascript.ReverseTemperature(RoutesPrefix.byNamePrefix());
  }

}
