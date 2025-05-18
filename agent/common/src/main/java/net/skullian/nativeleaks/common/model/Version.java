package net.skullian.nativeleaks.common.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Utility for parsing and then comparing a semantic version string
 * <p>
 * Created on 25/04/2025
 *
 * @author Preva1l
 */
@SuppressWarnings("unused")
public class Version implements Comparable<Version> {
    public final String VERSION_DELIMITER = ".";
    public static final String META_DELIMITER = "-";

    // Major, minor and patch version numbers
    private int[] versions = new int[]{};
    /**
     * -- GETTER --
     *  Get the version metadata.
     */
    @Getter
    private String metadata = "";
    private String metaSeparator = "";

    protected Version() {}

    private Version(String version, String metaDelimiter) {
        this.parse(version, metaDelimiter);
        this.metaSeparator = metaDelimiter;
    }

    /**
     * Create a new {@link Version} by parsing a string
     *
     * @param versionString The version string to parse
     * @return The {@link Version}
     * @implNote The default meta delimiter that will be used is {@link #META_DELIMITER}
     */
    public static Version fromString(String versionString) {
        return new Version(versionString, META_DELIMITER);
    }

    /**
     * Parses a version string, including metadata, with the specified delimiter
     *
     * @param version       The version string to parse
     * @param metaDelimiter The metadata delimiter
     */
    private void parse(String version, String metaDelimiter) {
        int metaIndex = version.indexOf(metaDelimiter);
        if (metaIndex > 0) {
            this.metadata = version.substring(metaIndex + 1);
            version = version.substring(0, metaIndex);
        }
        String[] versions = version.split(Pattern.quote(VERSION_DELIMITER));
        this.versions = Arrays.stream(versions).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Compare this {@link Version} to another {@link Version}
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this version is less than, equal to, or greater
     * than the other version in terms of the semantic major, minor and patch versioning standard.
     */
    @Override
    public int compareTo(Version other) {
        int length = Math.max(this.versions.length, other.versions.length);
        for (int i = 0; i < length; i++) {
            int a = i < this.versions.length ? this.versions[i] : 0;
            int b = i < other.versions.length ? other.versions[i] : 0;

            if (a < b) return -1;
            if (a > b) return 1;
        }

        return 0;
    }

    /**
     * Get the string representation of this {@link Version}
     *
     * @return The string representation of this {@link Version}
     */
    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(VERSION_DELIMITER);
        for (int version : this.versions) {
            joiner.add(String.valueOf(version));
        }
        return joiner + ((!this.metadata.isEmpty()) ? (this.metaSeparator + this.metadata) : "");
    }

    /**
     * Get the patch version number.
     *
     * @return The patch version number.
     */
    public int getPatch() {
        return this.versions[2];
    }
}
