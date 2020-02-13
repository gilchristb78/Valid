OLD INSTRUCTIONS DON'T READ

To execute the standard demonstration, you must first isntall IntelliJ and retrieve this project
from within IntelliJ.

1. Create a new Run Configuration (Run -> Edit Configurations...) and choose to add a new "sbt task".
   In the wizard dialog, (optionally) change the name "Unnamed" to something like "Run Service".
   Change the "tasks:" field to just be "run". Then Click "OK"

2. To run the service, select "Run Service" from the drop-down choice at the top-left of the ribbon bar.

3. Open up a browser on the same machine and enter "http://localhost:9000/narcotic" as the URL address.

4. After about 45 seconds, the resulting web page will have a button "Compute" which you must click.

5. A new set of tabs is created, and select the "Git" tab. There you will see instructions for cloning
   the generated repository. Copy this text.

6. Open up a DOS terminal window (or a MacOSx terminal) and change to the git folder in your home directory
   and then paste this command to execute. This will create the 'narcotic' folder which contains the
   generated variation

7. In a separate Eclipse instance, you can link these source files to be viewed, however they won't compile
   if you don't link to the 'standAlone.jar' file that is in this demo/ folder. Once you have properly
   configured your Eclipse project, the code will compile cleanly and then you can execute it.

   Note: be sure to link to the proper subdirectory in the git folder, which will be:

     git\VARIATION-NAME\src\main\java

8. A window will appear which contains the list of previously generated solitaire variations (including the
   one that was just generated). Select the desired variation and execute it.
