[Setup]
AppId={{1426EC8B-F11F-4CCF-8EFD-7F9CD92E9D2E}}
AppName=Aardvark
AppVersion=1.0.0
AppPublisher=EaterLabs
AppPublisherURL=https://eater.me
AppSupportURL=https://eater.me
AppUpdatesURL=https://eater.me
DefaultDirName={pf}\Aardvark
DefaultGroupName=Aardvark
AllowNoIcons=yes
LicenseFile=..\LICENSE
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64

[Files]
Source: "..\build\image\aardvark\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\icons\aardvark.ico"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{cm:UninstallProgram,Aardvark}"; Filename: "{uninstallexe}"
Name: "{userdesktop}\Aardvark"; Filename: "{app}/bin/javaw.exe"; WorkingDir: "{app}"; IconFilename: "{app}\aardvark.ico"; IconIndex: 0; Parameters: "-cp ""{app}/lib/*"" me.eater.emo.aardvark.Main"
Name: "{userstartmenu}/Aardvark\Aardvark"; Filename: "{app}/bin/javaw.exe"; WorkingDir: "{app}"; IconFilename: "{app}\aardvark.ico"; IconIndex: 0; Parameters: "-cp ""{app}/lib/*"" me.eater.emo.aardvark.Main"
