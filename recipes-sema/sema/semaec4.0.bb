FILESEXTRAPATHS:prepend := "${THISDIR}/semaec:"

SUMMARY = "SEMA Application"
DESCRIPTION = "At the heart of SEMA is the Board Management Controller (BMC) supporting SEMA functions. The SEMA Extended EAPI  provides access to all functions and can be integrated into the user’s own applications. The SEMA GUI and SEMA Command Line Interface  allow monitoring, control and use of the SEMA parameters and functions directly on your device for test and  demonstration  purpose "
HOMEPAGE = "https://www.adlinktech.com/en/SEMA.aspx"
SECTION = "Applications"

LICENSE = "CLOSED"

inherit module
DEPENDS += "  util-linux util-linux-libuuid"

SRCBRANCH = "sema-ec"
SRCREV = "ef2dd7bec03df852aa17f692df5579ce9718f2a2"
SRC_URI = "git://github.com/ADLINK/sema-linux.git;branch=${SRCBRANCH};protocol=http \
           file://Makefile \
           file://0001-updated-class_create-signature.patch \
           "

SRC_URI:append = " file://Makefile"

S = "${WORKDIR}/git"

CFLAGS:prepend = "-I${S}/lib"

do_compile:prepend() {
	rm -f ${S}/Makefile
	cp ${WORKDIR}/Makefile ${S}/Makefile
}

do_compile:append() {
	cd ${S}
	${CC} ${CFLAGS} ${LDFLAGS} -shared -fPIC -Wl,-soname,libsema.so ${S}/lib/backlight.c \
	${S}/lib/common.c \
	${S}/lib/boardinfo.c \
	${S}/lib/conv.c \
	${S}/lib/fan.c \
	${S}/lib/gpio.c \
	${S}/lib/i2c.c \
	${S}/lib/init.c \
	${S}/lib/storage.c \
	${S}/lib/watchdog.c -o ${S}/lib/libsema.so
  ${CC} ${CFLAGS} -Wall -L${S}/lib/ ${S}/app/main.c -lsema  -luuid  -o ${S}/semautil 
}

do_install:append() {
	install -d -m 0755 ${D}/lib64
	#ln -s -r ${D}/lib/ld-linux-x86-64.so.2  ${D}/lib64/ld-linux-x86-64.so.2 
	install -d -m 0755 ${D}/usr${base_libdir}
	install -d -m 0755 ${D}/usr${base_bindir}
	install -m 0755 ${S}/semautil ${D}/usr${base_bindir}/
	install -m 0755 ${S}/lib/libsema.so ${D}/usr${base_libdir}/
}


FILES:${PN} += "/etc /lib64 /usr${base_bindir}/semautil /usr${base_libdir}/*.so"
FILES_SOLIBSDEV = ""
do_package_qa() {
}

INSANE_SKIP_${PN} = "already-stripped"

KERNEL_MODULE_AUTOLOAD += "\
adl-ec \
adl-ec-bklight \
adl-ec-boardinfo \
adl-ec-i2c \
adl-ec-nvmem \
adl-ec-wdt \
adl-ec-hwmon \
adl-ec-vm \
adl-ec-vmem-sec \
adl-ec-nvmem-sec \
adl-ec-gpio \
"
