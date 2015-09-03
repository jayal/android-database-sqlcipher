LOCAL_PATH:= $(call my-dir)

EXTERNAL_PATH := ../external

LOCAL_CFLAGS += -DPACKED="__attribute__ ((packed))"

#TARGET_PLATFORM := android-8

ifeq ($(WITH_JIT),true)
	LOCAL_CFLAGS += -DWITH_JIT
endif

ifneq ($(USE_CUSTOM_RUNTIME_HEAP_MAX),)
  LOCAL_CFLAGS += -DCUSTOM_RUNTIME_HEAP_MAX=$(USE_CUSTOM_RUNTIME_HEAP_MAX)
endif

include $(CLEAR_VARS)

# expose the sqlcipher C API
LOCAL_CFLAGS += -DSQLITE_HAS_CODEC

LOCAL_SRC_FILES:= \
    net_sqlcipher_CursorWindow.cpp \
	net_sqlcipher_database_SQLiteCommon.cpp \
	net_sqlcipher_database_SQLiteConnection.cpp \
	net_sqlcipher_database_SQLiteDebug.cpp \
	net_sqlcipher_database_SQLiteGlobal.cpp \
	CursorWindow.cpp

LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE) \
	$(EXTERNAL_PATH)/sqlcipher \
	$(EXTERNAL_PATH)/openssl/include \
	$(EXTERNAL_PATH)/platform-frameworks-base/core/jni \
	$(EXTERNAL_PATH)/android-sqlite/android \
	$(EXTERNAL_PATH)/libnativehelper/include \
	$(EXTERNAL_PATH)/libnativehelper/include/nativehelper \
	$(EXTERNAL_PATH)/native/include \
	$(LOCAL_PATH)/include \
	$(EXTERNAL_PATH)/platform-frameworks-base/include \
	$(EXTERNAL_PATH)/icu4c/common \
	$(EXTERNAL_PATH)/platform-system-core/include

LOCAL_SHARED_LIBRARIES := \
	libcrypto \
	libssl \
	libsqlcipher \
	libsqlite3_android

LOCAL_CFLAGS += -U__APPLE__
LOCAL_LDFLAGS += -L../external/android-libs/$(TARGET_ARCH_ABI) -L../external/libs/$(TARGET_ARCH_ABI)/

# libs from the NDK
LOCAL_LDLIBS += -ldl -llog
# libnativehelper and libandroid_runtime are included with Android but not the NDK
LOCAL_LDLIBS += -lnativehelper -landroid_runtime -lutils -lbinder -lcutils
# these are build in the ../external section

LOCAL_LDFLAGS += -fuse-ld=bfd
LOCAL_LDLIBS  += -lsqlcipher_android
LOCAL_LDFLAGS += -L../obj/local/$(TARGET_ARCH_ABI)
LOCAL_LDLIBS  += -licui18n -licuuc

ifeq ($(WITH_MALLOC_LEAK_CHECK),true)
	LOCAL_CFLAGS += -DMALLOC_LEAK_CHECK
endif

LOCAL_MODULE:= libdatabase_sqlcipher

include $(BUILD_SHARED_LIBRARY)

include $(call all-makefiles-under,$(LOCAL_PATH))
