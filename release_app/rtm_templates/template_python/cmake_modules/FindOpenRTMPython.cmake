
set(OMNIORB_FOUND FALSE)
set(OPENRTM_FOUND FALSE)


if(UNIX)
    include(FindPkgConfig)

    # omniORB
    pkg_check_modules(OMNIORB REQUIRED "omniORB4")
    if(NOT OMNIORB_DIR)
        if(OMNIORB_FOUND)
            set(OMNIORB_DIR "${OMNIORB_PREFIX}")
        endif()
        set(OMNIORB_DIR "${OMNIORB_DIR}" CACHE PATH "omniORB root directory")
    endif()

    # OpenRTM-aist
    pkg_check_modules(OPENRTM REQUIRED "openrtm-aist")
    if(NOT OPENRTM_DIR)
        if(OPENRTM_FOUND)
            set(OPENRTM_DIR "${OPENRTM_PREFIX}")
        endif()
        set(OPENRTM_DIR "${OPENRTM_DIR}" CACHE PATH "OpenRTM-aist root directory")
    endif()

    string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)" "\\1"
           OPENRTM_VERSION_MAJOR "${OPENRTM_VERSION}")
    string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)" "\\2"
           OPENRTM_VERSION_MINOR "${OPENRTM_VERSION}")
    string(REGEX REPLACE "([0-9]+)\\.([0-9]+)\\.([0-9]+)" "\\3"
           OPENRTM_VERSION_PATCH "${OPENRTM_VERSION}")

    # IDL Compiler
    set(OPENRTM_IDLC "omniidl")
    set(OPENRTM_IDLFLAGS "-bpython")

endif(UNIX)

if(WIN32)
    # omniORB
    if(NOT OMNIORB_DIR)
        if(NOT $ENV{OMNI_ROOT} STREQUAL "")
            set(OMNIORB_DIR "$ENV{OMNI_ROOT}")
            set(OMNIORB_FOUND TRUE)
        endif()
        set(OMNIORB_DIR "${OMNIORB_DIR}" CACHE PATH "omniORB root directory")
        if(NOT OMNIORB_FOUND)
            message(FATAL_ERROR "omniORB not found.")
        endif()
    endif()

    # omniORB version
    file(GLOB _vers RELATIVE "${OMNIORB_DIR}" "${OMNIORB_DIR}/THIS_IS_OMNIORB*")
    if("${_vers}" STREQUAL "")
        message(FATAL_ERROR "omniORB version file not found.")
    endif()

    if("${_vers}" MATCHES "THIS_IS_OMNIORB")
        set(OMNIORB_VERSION "${_vers}")
        string(REGEX REPLACE "THIS_IS_OMNIORB_" ""
               OMNIORB_VERSION "${OMNIORB_VERSION}")
        string(REGEX REPLACE "[_]" "."
               OMNIORB_VERSION "${OMNIORB_VERSION}")
        string(REGEX REPLACE "[.]" ""
               OMNIORB_VERSION_NUM "${OMNIORB_VERSION}")
    endif()

    # OpenRTM-aist
    if(NOT OPENRTM_DIR)
        if(NOT $ENV{RTM_ROOT} STREQUAL "")
            set(OPENRTM_DIR "$ENV{RTM_ROOT}")
            set(OPENRTM_FOUND TRUE)
        endif()
        set(OPENRTM_DIR "${OPENRTM_DIR}" CACHE PATH "OpenRTM-aist root directory")
        if(NOT OPENRTM_FOUND)
            message(FATAL_ERROR "OpenRTM-aist not found.")
        endif()
    endif()

    # OpenRTM-aist version
    set(OPENRTM_VERSION "${OPENRTM_DIR}")
    string(REGEX REPLACE ".*OpenRTM-aist/" "" OPENRTM_VERSION "${OPENRTM_VERSION}")
    string(REGEX REPLACE "([0-9]+)\\.([0-9]+)" "\\1" OPENRTM_VERSION_MAJOR "${OPENRTM_VERSION}")
    string(REGEX REPLACE "([0-9]+)\\.([0-9]+)" "\\2" OPENRTM_VERSION_MINOR "${OPENRTM_VERSION}")
    set(OPENRTM_VERSION_PATCH "0")
    set(OPENRTM_VERSION "${OPENRTM_VERSION_MAJOR}.${OPENRTM_VERSION_MINOR}.${OPENRTM_VERSION_PATCH}")

    # IDL Compiler
    set(OPENRTM_IDLC "omniidl")
    set(OPENRTM_IDLFLAGS -bpython)

endif(WIN32)

message(STATUS "FindOpenRTMPython setup done.")

message(STATUS "  OMNIORB_DIR=${OMNIORB_DIR}")
message(STATUS "  OMNIORB_VERSION=${OMNIORB_VERSION}")

message(STATUS "  OPENRTM_DIR=${OPENRTM_DIR}")
message(STATUS "  OPENRTM_VERSION=${OPENRTM_VERSION}")
message(STATUS "  OPENRTM_VERSION_MAJOR=${OPENRTM_VERSION_MAJOR}")
message(STATUS "  OPENRTM_VERSION_MINOR=${OPENRTM_VERSION_MINOR}")
message(STATUS "  OPENRTM_VERSION_PATCH=${OPENRTM_VERSION_PATCH}")

message(STATUS "  OPENRTM_IDLC=${OPENRTM_IDLC}")
message(STATUS "  OPENRTM_IDLFLAGS=${OPENRTM_IDLFLAGS}")
