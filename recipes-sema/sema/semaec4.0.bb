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
           "

SRC_URI:append = " file://Makefile"

S = "${WORKDIR}/git"

CFLAGS:prepend = "-I${WORKDIR}/git/lib"

do_compile:prepend() {
	rm -r ${WORKDIR}/git/Makefile
	cp ${WORKDIR}/Makefile ${WORKDIR}/git/Makefile
}

do_compile:append() {
	cd ${WORKDIR}/git
	${CC} ${CFLAGS} ${LDFLAGS} -shared -fPIC -Wl,-soname,libsema.so ${WORKDIR}/git/lib/backlight.c \
	${WORKDIR}/git/lib/common.c \
	${WORKDIR}/git/lib/boardinfo.c \
	${WORKDIR}/git/lib/conv.c \
	${WORKDIR}/git/lib/fan.c \
	${WORKDIR}/git/lib/gpio.c \
	${WORKDIR}/git/lib/i2c.c \
	${WORKDIR}/git/lib/init.c \
	${WORKDIR}/git/lib/storage.c \
	${WORKDIR}/git/lib/watchdog.c -o ${WORKDIR}/git/lib/libsema.so
  	${CC} ${CFLAGS} -Wall -L${WORKDIR}/git/lib/ ${WORKDIR}/git/app/main.c -lsema  -luuid  -o ${WORKDIR}/git/semautil 
}

do_install:append() {
	install -d -m 0755 ${D}/lib64
	ln -s -r ${D}/lib/ld-linux-x86-64.so.2  ${D}/lib64/ld-linux-x86-64.so.2 
	install -d -m 0755 ${D}/usr${base_libdir}
	install -d -m 0755 ${D}/usr${base_bindir}
	install -m 0755 ${WORKDIR}/git/semautil ${D}/usr${base_bindir}/
	install -m 0755 ${WORKDIR}/git/lib/libsema.so ${D}/usr${base_libdir}/
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

