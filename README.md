##File manager

Simple stateless application for creating file duplicate in required folder.

###Stack
* jdk_1.8
* maven
* javaFx

###Behavior
User selects folder with files what need to be duplicate. And target folder as destination.

Project will create a copy of such files on folder with maximum match.
In case of conflict of match percentage or low match rate. It's will create a copy on spesial report folder.

Report and report folder will be created in root files' directory.  And includes: 
* report by itself
* invalid files what can't or may not be operational.