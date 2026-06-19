package io.gorse.gorse4j.connect;

final class GorseVersion {

    private GorseVersion() {
    }

    static String version() {
        String version = GorseVersion.class.getPackage().getImplementationVersion();
        return version == null ? "0.5.0" : version;
    }
}
