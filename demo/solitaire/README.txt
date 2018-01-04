Instructions for compiling and running solitaire variations

1. Start the Inhabitation service as an sbt task "nextgen-solitaire/run".

2. Launch a web browser and enter request, such as "http://localhost:9000/idiot"

  To see the full set of available variations, review the "routes" file which is
  found in "src/main/resources/routes"

3. When the inhabitation completes, click on the "Git" tab and after a short computation
   new instructions appear with information about the git repository

4. Check out the variation git repository within the "demo/solitaire" folder

  git clone -b variation_0 http://localhost:9000/idiot/idiot.git

5. Then back in the "demo" folder, invoke one of the targets of the Makefile,
   such as:

   make run-idiot

   And this will compile and launch the variation.

