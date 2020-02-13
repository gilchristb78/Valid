Instructions for compiling and running solitaire variations

1. Start the Inhabitation service as an sbt task "nextgen-solitaire/run".

2. Launch a web browser and enter request, such as "http://localhost:9000/fan/shamrocks"

  To see the full set of available variations, review the "routes" file which is
  found in "src/main/resources/routes"

  it is a bit more complicated, since solitaire variations may be stand alone or represent
  a family of potential variations. Here Fan has numerous variations...

     * http://localhost:9000/fan/shamrocks
     * http://localhost:9000/fan/scotchpatience  [** This one doesn't fully work yet **]
     * ....

3. When the inhabitation completes, click on the "Compute" button and after a short computation
   new instructions appear; click the "Git" tab to find information about the git repository

4. Check out the variation git repository

  git clone -b variation_0 http://localhost:9000/fan/shamrocks/shamrocks.git

5. This code will only compile if you have the standAlone.jar file available. My recommendation
   is to bring this checked-out code into Eclipse, for example, and then import the standAlone.jar file
   and then you can work.

6. The starting point will be to develop the boilerplate code in the first place.
