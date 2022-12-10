// ITestRoot.aidl
package com.teamuxh.uxh_toolkit;

// Declare any non-default types here with import statements

interface ITestRoot {
    /**     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     int getUid();
     int getAppPid(String packageName);
     int getLibAddress(int pid,String libName);
     String[] getLoadedLibrary(int pid);
}