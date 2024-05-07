# Detailed Configuration

This file is written for the whole project configuration including the [backend](#Backend)and [frontend](#Frontend) since we are somehow back-front separated.

## Backend

- [Configure JDK-17](#1-configure-jdk-17)
- [Configure Apache Tomcat Server](#2-configure-apache-tomcat-server)
- [Configure JetBrains IntelliJ IDEA](#3-configure-jetbrains-intellij-idea)
- [Configure MySQL and the Connector](#4-configure-mysql-and-the-connector)
- [Test](#5-test)

### 1. Configure JDK-17

1. Go [here](https://www.oracle.com/java/technologies/downloads/) and choose "JDK 17", select the Archive/Installer according to your OS.
2. If you downloaded an Installer, it probably has added the jdk to your environment path; if not, remember to add the "bin/" directory to the path variable.
3. Check whether you successfully have configured JDK-17 by opening a new terminal and type in:
```shell
 java --version
```

### 2. Configure Apache Tomcat Server

Go [here](https://tomcat.apache.org/) and choose "Tomcat 9" in the left sidebar, select the Archive/Installer according to your OS.

### 3. Configure JetBrains IntelliJ IDEA

1. **Configure JDK**: Select "File" > "Project Structure..."(or press "shift+ctrl+alt+s") > "Project Settings" > "Project", select the SDK to the JDK-17 directory you have downloaded before.
2. **Configure Tomcat Server**: Select "File" > "Settings..."(or press "ctrl+alt+s") > "Build, Execution, Deployment" > "Application Servers", click the "+" in the top of middle blank, and choose "Tomcat Server", then select the Tomcat directory you have downloaded before.
3. **Configure other dependencies**: Select "File" > "Project Structure..."(or press "shift+ctrl+alt+s") > "Project Settings" > "Libraries", click the "+" in the top of middle blank, and click "Java" if you wanted adding from local; click "from maven" if you wanted to download from the Internet. Add the dependencies we mentioned before.

### 4. Configure MySQL and the Connector

1. **Creating tables and Inserting data**: Connect to MySQL Server in Command Line, create a new user(or not, then the user should be "root") and a database granted to him. Prepare the .sql files we provide, type in:
```sql
source your/path/to/create_tables.sql;
...
source your/path/to/insert_tables.sql;
...
```
2. **Configure the Connector in the backend**: Open the [Connector file](./src/util/DBConnector.java), and check line 24-26, replace the username, database name, password with your own.

### 5. Test

If all you have done with these, then:
1. Run the whole Java Project through the triangle on the top bar, you will see one "HTTP 500" page shows up, don't worry and follow;
2. Modify the url from "http://localhost:8080/JavaWebMoiveDB_Web_exploded/home" to "http://localhost:8080/JavaWebMoiveDB_Web_exploded/home?type=popular&Page=1", then press enter.
3. The correct way you will get a bunch of JSON strings begin with 
```json
[{"movie_id":211672,"budget":74000000,...
```

## Frontend

- [Configure npm](#1-configure-npm)
- [Configure Visual Studio Code extension](#2-configure-visual-studio-code-extension)
- [Test](#3-test)

### 1. Configure npm

1. You can directly install the latest release of npm through `curl`:
```shell
curl -qL https://www.npmjs.com/install.sh | sh
```
2. **(Recommended)** Or you can install nvm(short for npm version manager) to install npm:
```shell
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash // install nvm.
nvm --version                                   // check whether nvm is successfully installed.
nvm list                                          // list all the npm version you have locally.
nvm install <version>                              // download the npm with version you wanted.
nvm use <version>               // select the npm version from the versions you have installed.
```

### 2. Configure Visual Studio Code extension

Open VSCode and check the extensions, search for "Svelte Intellisense" and click "Install".

### 3. Configure Browser

To enable **cross-origin resource request**(which is inevitable since we separate the back and front ), we have to disable the default web-security policy of modern browsers, which is used to defend **CSXF attack** from hackers.
Here we only show how to disable Chrome's web-security policy:
- **Method 1**: 
```shell
chrome.exe --user-data-dir="c:/ChromeForLab3" --disable-web-security
```
- **Method 2(Recommended)**: Copy one shortcut of Chrome to the desktop(or anywhere), right click it and select "Attribute", and replace the "Target" blank with the above shell code. 
- **Test**: In correct way you will see a warning below the url blank when opening the Chrome.

### 3. Test

If you have all done with the back/frontend configurations, open terminal in the root directory of the frontend project, and type in:
```shell
npm install
...
npm run dev
> moviedb@0.0.1 dev
> vite dev



  VITE v4.5.1  ready in xxxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h to show help
```
Then check the "http://localhost:5173/" in the Chrome that we have configured, in correct way you will see our home page.
