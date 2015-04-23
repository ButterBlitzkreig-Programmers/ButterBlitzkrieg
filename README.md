# Butter Blitzkrieg

Follow these instructions from the LibGDX wiki to setup Eclipse.

>Setting up Eclipse.
>To develop your application via Eclipse, you need to install the following pieces of software.

    Java Development Kit 7+ (JDK) (6 will not work!)
    
    Eclipse, the "Eclipse IDE for Java Developers" is usually sufficient.
    
    Android SDK, you only need the SDK, not the ADT bundle, which includes Eclipse. Install the API 20 platform via the SDK Manager.
    
    Android Development Tools for Eclipse, aka ADT Plugin. Go to Help -> Install New Software and use this update site: https://dl-ssl.google.com/android/eclipse/
    
    Eclipse Integration Gradle, use this update site: http://dist.springsource.com/snapshot/TOOLS/gradle/nightly

Clone the git repository using your preferred git program. GitHub for Windows works well.

Once Eclipse is setup, go to File -> Import -> Gradle Project. Select the directory to which you cloned the git repository. Click "Build Model" then let it finish. Uncheck the "Run After" check box, then finish. The projects should now be imported. Expand the ButterBlitz-desktop project. Right click the "assets" folder, mouse over build path, then select "use as source folder".

Whenever assets are updated or changed, go to Project -> Clean and click OK. Failure to do so will result in errors.

The relevant source files are located under the ButterBlitz-core project in the src folder. To launch the project, right click the ButterBlitz-desktop project, mouse over "run as", then click "Java application". Click "DesktopLauncher" then press the OK button in the resulting popup window. The game should now launch.
