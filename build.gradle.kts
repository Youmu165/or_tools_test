plugins {
    id("java")
}

group = "p4_group_8_repo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.ortools:ortools-java:9.11.4210")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("org.apache.commons:commons-math3:3.6.1")
}

tasks.test {
    useJUnitPlatform()

}