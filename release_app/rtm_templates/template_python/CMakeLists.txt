cmake_minimum_required(VERSION 2.8)
if(POLICY CMP0040)
  cmake_policy(SET CMP0040 OLD)
endif()
if(POLICY CMP0053)
  cmake_policy(SET CMP0053 OLD)
endif()

project(template_python)
string(TOLOWER ${PROJECT_NAME} PROJECT_NAME_LOWER)
include("${PROJECT_SOURCE_DIR}/cmake/utils.cmake")
set(PROJECT_VERSION 1.0.0 CACHE STRING "template_python version")
set(UPGRADE_GUID "")
DISSECT_VERSION()
set(PROJECT_SHORT_VER ${PROJECT_VERSION_MAJOR}${PROJECT_VERSION_MINOR}${PROJECT_VERSION_REVISION})
set(PROJECT_DESCRIPTION "ModuleDescription")
set(PROJECT_VENDOR "VenderName")
set(PROJECT_MAINTAINER "unknown")
set(PROJECT_TYPE "python/Category")

find_package(OpenRTM)
set(RTM_VER ${OPENRTM_VERSION})
set(RTM_SHORT_VER ${OPENRTM_VERSION_MAJOR}${OPENRTM_VERSION_MINOR}${OPENRTM_VERSION_PATCH})

function(get_dist ARG0)
  if(NOT ${CMAKE_SYSTEM_NAME} MATCHES "Linux")
    set(${ARG0} ${CMAKE_SYSTEM_NAME} PARENT_SCOPE)
    return()
  endif()
  foreach(dist Debian Ubuntu RedHat Fedora CentOS Raspbian)
    execute_process(
      COMMAND grep ${dist} -s /etc/issue /etc/os-release /etc/redhat-release /etc/system-release
      OUTPUT_VARIABLE dist_name
      )
    if(${dist_name} MATCHES ${dist})
      set(${ARG0} ${dist} PARENT_SCOPE)
      return()
    endif()
  endforeach()
endfunction(get_dist)

function(get_pkgmgr ARG0)
  get_dist(DIST_NAME)
  if(${DIST_NAME} MATCHES "Debian" OR
     ${DIST_NAME} MATCHES "Ubuntu" OR
     ${DIST_NAME} MATCHES "Raspbian")
     set(${ARG0} "DEB" PARENT_SCOPE)
     return()
  endif()
  if(${DIST_NAME} MATCHES "RedHat" OR
     ${DIST_NAME} MATCHES "Fedora" OR
     ${DIST_NAME} MATCHES "CentOS")
     set(${ARG0} "RPM" PARENT_SCOPE)
     return()
  endif()
endfunction(get_pkgmgr)

get_dist(DIST_NAME)
MESSAGE(STATUS "Distribution is ${DIST_NAME}")

get_pkgmgr(PKGMGR)
if(PKGMGR AND NOT LINUX_PACKAGE_GENERATOR)
 set(LINUX_PACKAGE_GENERATOR ${PKGMGR})
 if(${PKGMGR} MATCHES "DEB")
   execute_process(COMMAND dpkg --print-architecture
     OUTPUT_VARIABLE CPACK_DEBIAN_PACKAGE_ARCHITECTURE
     OUTPUT_STRIP_TRAILING_WHITESPACE)
   message(STATUS "Package manager is ${PKGMGR}. Arch is ${CPACK_DEBIAN_PACKAGE_ARCHITECTURE}.")
 endif()
 if(${PKGMGR} MATCHES "RPM")
   execute_process(COMMAND uname "-m"
     OUTPUT_VARIABLE CPACK_RPM_PACKAGE_ARCHITECTURE
     OUTPUT_STRIP_TRAILING_WHITESPACE)
   message(STATUS "Package manager is ${PKGMGR}. Arch is ${CPACK_RPM_PACKAGE_ARCHITECTURE}.")
 endif()
endif()

option(BUILD_DOCUMENTATION "Build the documentation" OFF)


set(EXEC_FILES ""
               template_python.py
)

set(OTHER_SRCS CMakeLists.txt
               RTC.xml template_python.conf rtc.conf)

# check python installed
find_package(PythonInterp)

if(WIN32)
   set(OPENRTM_SHARE_PREFIX "OpenRTM-aist/${RTM_VER}/Components/${PROJECT_TYPE}")
   set(INSTALL_PREFIX ${PROJECT_NAME})
   if(CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT)
      set(CMAKE_INSTALL_PREFIX "${OPENRTM_DIR}Components/${PROJECT_TYPE}/${PROJECT_NAME}" CACHE PATH "..." FORCE)
   endif()
else()
   set(OPENRTM_SHARE_PREFIX "share/openrtm-${OPENRTM_VERSION_MAJOR}.${OPENRTM_VERSION_MINOR}")
   set(INSTALL_PREFIX "${OPENRTM_SHARE_PREFIX}/components/${PROJECT_TYPE}/${PROJECT_NAME}")
endif()


foreach(target ${EXEC_FILES})
  add_custom_command( TARGET ${PROJECT_NAME} POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy
        ${PROJECT_SOURCE_DIR}/${target} ${PROJECT_BINARY_DIR}
  )
endforeach()

install(PROGRAMS ${EXEC_FILES} DESTINATION "${INSTALL_PREFIX}"
        COMPONENT component)

install(FILES ${PROJECT_SOURCE_DIR}/RTC.xml DESTINATION ${INSTALL_PREFIX}
        COMPONENT component)


# add uninstall target
configure_file(
    "${CMAKE_CURRENT_SOURCE_DIR}/cmake/uninstall_target.cmake.in"
    "${CMAKE_CURRENT_BINARY_DIR}/uninstall_target.cmake"
    IMMEDIATE @ONLY)
add_custom_target(uninstall COMMAND
    ${CMAKE_COMMAND} -P ${CMAKE_CURRENT_BINARY_DIR}/cmake_uninstall.cmake)

if(WIN32)
    # start menu
    set_property(INSTALL "${INSTALL_PREFIX}/${PROJECT_NAME}.py"
        PROPERTY CPACK_START_MENU_SHORTCUTS "${PROJECT_NAME}.py")
endif()

# target settings for document
if(BUILD_DOCUMENTATION)
    add_subdirectory(doc)
endif(BUILD_DOCUMENTATION)


set(cpack_options "${PROJECT_BINARY_DIR}/cpack_options.cmake")
configure_file("${PROJECT_SOURCE_DIR}/cmake/cpack_options.cmake.in"
    ${cpack_options} @ONLY)

set(CPACK_PROJECT_CONFIG_FILE ${cpack_options})
include(${CPACK_PROJECT_CONFIG_FILE})
include(CPack)
