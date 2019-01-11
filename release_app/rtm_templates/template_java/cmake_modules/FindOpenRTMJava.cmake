
set(OPENRTM_FOUND FALSE)


if(UNIX)
    # OpenRTM-aist
    if(NOT OPENRTM_DIR)
        if(NOT $ENV{RTM_JAVA_ROOT} STREQUAL "")
            set(OPENRTM_DIR "$ENV{RTM_JAVA_ROOT}")
            set(OPENRTM_FOUND TRUE)
        endif()
        set(OPENRTM_DIR "${OPENRTM_DIR}" CACHE PATH "OpenRTM-aist root directory")
        if(NOT OPENRTM_FOUND)
            message(FATAL_ERROR "OpenRTM-aist not found.")
        endif()
    endif()

    # OpenRTM-aist version
    file(GLOB _vers RELATIVE "${OPENRTM_DIR}/jar" "${OPENRTM_DIR}/jar/OpenRTM-aist-*.jar")
    if("${_vers}" STREQUAL "")
        message(FATAL_ERROR "OpenRTM jar file not found.")
    endif()

    if("${_vers}" MATCHES "OpenRTM-aist-")
        string(REGEX REPLACE "OpenRTM-aist-" "" _vers "${_vers}")
        string(REGEX REPLACE "\\.jar$" "" _vers "${_vers}")
        set(OPENRTM_VERSION "${_vers}")
        string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)"
               "\\1" OPENRTM_VERSION_MAJOR "${_vers}")
        string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)"
               "\\2" OPENRTM_VERSION_MINOR "${_vers}")
        string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)"
               "\\3" OPENRTM_VERSION_PATCH "${_vers}")
    endif()

    file(GLOB _jars "${OPENRTM_DIR}/jar/*.jar")
    string(REPLACE ";" ":" OPENRTM_CLASSPATH "${_jars}")

endif(UNIX)

if(WIN32)
    # OpenRTM-aist
    if(NOT OPENRTM_DIR)
        if(NOT $ENV{RTM_JAVA_ROOT} STREQUAL "")
            set(OPENRTM_DIR "$ENV{RTM_JAVA_ROOT}")
            set(OPENRTM_FOUND TRUE)
        endif()
        set(OPENRTM_DIR "${OPENRTM_DIR}" CACHE PATH "OpenRTM-aist root directory")
        if(NOT OPENRTM_FOUND)
            message(FATAL_ERROR "OpenRTM-aist not found.")
        endif()
    endif()

    # OpenRTM-aist version
    file(GLOB _vers RELATIVE "${OPENRTM_DIR}/jar" "${OPENRTM_DIR}/jar/OpenRTM-aist-*.jar")
    if("${_vers}" STREQUAL "")
        message(FATAL_ERROR "OpenRTM jar file not found.")
    endif()

    if("${_vers}" MATCHES "OpenRTM-aist-")
        string(REGEX REPLACE "OpenRTM-aist-" "" _vers "${_vers}")
        string(REGEX REPLACE "\\.jar$" "" _vers "${_vers}")
        set(OPENRTM_VERSION "${_vers}")
        string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)"
               "\\1" OPENRTM_VERSION_MAJOR "${_vers}")
        string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)"
               "\\2" OPENRTM_VERSION_MINOR "${_vers}")
        string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)"
               "\\3" OPENRTM_VERSION_PATCH "${_vers}")
    endif()

    file(GLOB _jars "${OPENRTM_DIR}/jar/*.jar")
    set(OPENRTM_CLASSPATH "${_jars}")

endif(WIN32)

message(STATUS "FindOpenRTMJava setup done.")

message(STATUS "  OPENRTM_DIR=${OPENRTM_DIR}")
message(STATUS "  OPENRTM_VERSION=${OPENRTM_VERSION}")
message(STATUS "  OPENRTM_VERSION_MAJOR=${OPENRTM_VERSION_MAJOR}")
message(STATUS "  OPENRTM_VERSION_MINOR=${OPENRTM_VERSION_MINOR}")
message(STATUS "  OPENRTM_VERSION_PATCH=${OPENRTM_VERSION_PATCH}")
message(STATUS "  OPENRTM_CLASSPATH=${OPENRTM_CLASSPATH}")
