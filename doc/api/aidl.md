## aidl

Generates java class source code from android aidl file.

 * String __apiLevel__ - Android API level of platform which framework.aidl
file should be used.
 * String __buildToolsVersion__ - Version of android build tools that should.
be used (this is the name of the directory in android sdk that contains aidl
binary file - for example "17.0.0").
 * File __interfaceFile__ - aidl file which is converted to java class.

Returns __File__ containing java source code of generated class.

### examples

Generates java class source code for inapp billing.
```
aidl-file = file("//aidl/com/android/vending/billing/IInAppBillingService.aidl");
src = aidl(apiLevel="19", buildToolsVersion="17.0.0", interfaceFile=aidl-file);
```
