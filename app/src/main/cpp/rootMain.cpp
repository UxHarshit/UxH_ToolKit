#include <jni.h>
#include <dirent.h>
#include <fstream>
#include <iostream>
#include <string>
#include <unistd.h>
#include <list>
#include <vector>
#include <android/log.h>
#include <unordered_set>
#include <sstream>
#include <set>
#include <algorithm>
#include <map>

//
// Created by hurri on 23-11-2022.
//


extern "C"
JNIEXPORT jint JNICALL
Java_com_teamuxh_uxh_1toolkit_constant_NativeRoot_00024Companion_nativeGetUid(JNIEnv *env,
                                                                              jobject thiz) {
    return (int) getuid();
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_teamuxh_uxh_1toolkit_constant_NativeRoot_00024Companion_nativeGetPid(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jstring package_name) {
    const char *packagename = env->GetStringUTFChars(package_name, nullptr);
    int id = -1;
    DIR *dir;
    FILE *fp;
    char filename[512];
    char cmdline[256];
    struct dirent *entry;
    dir = opendir("/proc");
    while ((entry = readdir(dir)) != NULL) {
        id = atoi(entry->d_name);
        if (id != 0) {
            sprintf(filename, "/proc/%d/cmdline", id);
            fp = fopen(filename, "r");
            if (fp) {
                fgets(cmdline, sizeof(cmdline), fp);
                fclose(fp);

                if (strcmp(packagename, cmdline) == 0) {
                    return id;
                }
            }
        }
    }
    closedir(dir);
    return -1;
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_teamuxh_uxh_1toolkit_constant_NativeRoot_00024Companion_getModuleAddress(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jint pid,
                                                                                  jstring lib_name) {
    const char *module_name = env->GetStringUTFChars(lib_name, 0);
    FILE *fp;
    long addr = 0;
    char *pch;
    char filename[32];
    char line[1024];
    snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    fp = fopen(filename, "r");
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, module_name)) {
                pch = strtok(line, "-");
                addr = strtoul(pch, NULL, 16);
                if (addr == 0x8000)
                    addr = 0;
                break;
            }
        }
        fclose(fp);

    }
    return addr;
}

void test() {

}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_teamuxh_uxh_1toolkit_constant_NativeRoot_00024Companion_getLoadedLibraries(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jint pid) {
    jobjectArray pArray = nullptr;
    char filename[32];
    snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    std::ifstream file(filename);
    std::unordered_set<std::string> values;
    std::string line;
    while (std::getline(file, line)) {
        std::istringstream iss(line);
        std::string word;
        for (int i = 0; i < 6; ++i) {
            iss >> word;
            if (i == 5 && word.find(".so") != std::string::npos)
                values.insert(word);
        }
    }
    file.close();
    std::vector<std::string> vecValues(values.begin(), values.end());
    std::sort(vecValues.begin(), vecValues.end());
    pArray = env->NewObjectArray(vecValues.size(), env->FindClass("java/lang/String"),
                                 nullptr);
    for (int i = 0; i < vecValues.size(); i++) {
        env->SetObjectArrayElement(pArray, i, env->NewStringUTF(vecValues[i].c_str()));
    }

    return pArray;
}