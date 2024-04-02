package io.github.blankbro.jar2maven;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    /**
     * https://central.sonatype.org/search/rest-api-guide/
     */
    private static final String MAVEN_SEARCH_URL = "https://search.maven.org/solrsearch/select?q=a:{artifactId}%20v:{version}&rows=1&wt=json";

    private static void search(List<String> jarList) throws IOException {
        List<String> noMatched = new ArrayList<>();
        for (String jarFullName : jarList) {
            // 从jar name 中提取关键信息
            String jarName = jarFullName.replace(".jar", "");
            String[] jarNameSplit = jarName.split("-");
            String version = jarNameSplit[jarNameSplit.length - 1];
            if ("optional".equalsIgnoreCase(version)) {
                jarName = jarName.replace("-optional", "");
                version = jarNameSplit[jarNameSplit.length - 2];
            } else if ("snapshot".equalsIgnoreCase(version) || "release".equalsIgnoreCase(version)) {
                version = jarNameSplit[jarNameSplit.length - 2] + "-" + version;
            }
            String artifactId = jarName.replace("-" + version, "");

            // 请求 maven search
            String url = MAVEN_SEARCH_URL.replace("{artifactId}", artifactId);
            url = url.replace("{version}", version);
            String searchResult = HttpUtil.httpGet(url);
            System.out.println(searchResult);

            // 获取搜索结果
            JSONObject searchResultObj = JSON.parseObject(searchResult);
            JSONObject response = searchResultObj.getJSONObject("response");
            JSONArray docs = response.getJSONArray("docs");
            if (docs.isEmpty()) {
                System.err.println(artifactId + "没有搜索到任何 maven 坐标");
                continue;
            }

            boolean matched = false;
            for (int i = 0; i < docs.size(); i++) {
                JSONObject doc = docs.getJSONObject(i);
                if (!artifactId.equalsIgnoreCase(doc.getString("a"))
                        || !version.equalsIgnoreCase(doc.getString("v"))) {
                    continue;
                }
                matched = true;
                String groupId = doc.getString("g");
                System.out.println("<dependency>");
                System.out.println("\t<groupId>" + groupId + "</groupId>");
                System.out.println("\t<artifactId>" + artifactId + "</artifactId>");
                System.out.println("\t<version>" + version + "</version>");
                System.out.println("</dependency>");
                break;
            }

            if (!matched) {
                noMatched.add(jarFullName);
            }
        }

        if (!noMatched.isEmpty()) {
            System.out.println("没有匹配的到的jar有: " + noMatched);
        }
    }

    public static void main(String[] args) throws IOException {
        search(Arrays.asList(
                "HikariCP-4.0.3.jar",
                "aspectjweaver-1.9.7.jar",
                "auto-service-annotations-1.0.1.jar",
                "checkpoint-storage-api-2.3.3.jar",
                "checkpoint-storage-local-file-2.3.3.jar",
                "classmate-1.3.1.jar",
                "commons-codec-1.13.jar",
                "commons-collections4-4.4.jar",
                "commons-compress-1.20.jar",
                "commons-io-2.11.0.jar",
                "commons-lang3-3.4.jar",
                "config-1.3.3.jar",
                "datasource-elasticsearch-1.0.0.jar",
                "datasource-hive-1.0.0.jar",
                "datasource-jdbc-clickhouse-1.0.0.jar",
                "datasource-jdbc-hive-1.0.0.jar",
                "datasource-jdbc-mysql-1.0.0.jar",
                "datasource-jdbc-oracle-1.0.0.jar",
                "datasource-jdbc-postgresql-1.0.0.jar",
                "datasource-jdbc-redshift-1.0.0.jar",
                "datasource-jdbc-sqlserver-1.0.0.jar",
                "datasource-jdbc-starrocks-1.0.0.jar",
                "datasource-jdbc-tidb-1.0.0.jar",
                "datasource-kafka-1.0.0.jar",
                "datasource-mysql-cdc-1.0.0.jar",
                "datasource-plugins-api-1.0.0.jar",
                "datasource-s3-1.0.0.jar",
                "datasource-sqlserver-cdc-1.0.0.jar",
                "datasource-starrocks-1.0.0.jar",
                "gson-2.8.6.jar",
                "guava-19.0.jar",
                "h2-2.1.214.jar",
                "hazelcast-5.1.jar",
                "hibernate-validator-6.2.2.Final.jar",
                "jackson-annotations-2.12.6.jar",
                "jackson-core-2.12.6.jar",
                "jackson-databind-2.12.6.jar",
                "jackson-datatype-jdk8-2.13.3.jar",
                "jackson-datatype-jsr310-2.13.3.jar",
                "jackson-module-parameter-names-2.13.3.jar",
                "jakarta.annotation-api-1.3.5.jar",
                "jakarta.servlet-api-4.0.4.jar",
                "jakarta.validation-api-2.0.2.jar",
                "jakarta.websocket-api-1.1.2.jar",
                "jboss-logging-3.4.1.Final.jar",
                "jcl-over-slf4j-1.7.25.jar",
                "jcommander-1.81.jar",
                "jetty-continuation-9.4.46.v20220331.jar",
                "jetty-http-9.4.46.v20220331.jar",
                "jetty-io-9.4.46.v20220331.jar",
                "jetty-security-9.4.46.v20220331.jar",
                "jetty-server-9.4.46.v20220331.jar",
                "jetty-servlet-9.4.46.v20220331.jar",
                "jetty-servlets-9.4.46.v20220331.jar",
                "jetty-util-9.4.46.v20220331.jar",
                "jetty-util-ajax-9.4.46.v20220331.jar",
                "jetty-webapp-9.4.46.v20220331.jar",
                "jetty-xml-9.4.46.v20220331.jar",
                "jjwt-api-0.10.7.jar",
                "jjwt-impl-0.10.7.jar",
                "jjwt-jackson-0.10.7.jar",
                "jsqlparser-4.4.jar",
                "jsr305-3.0.0.jar",
                "log4j-api-2.17.1.jar",
                "log4j-over-slf4j-1.7.25.jar",
                "log4j-to-slf4j-2.17.1.jar",
                "logback-classic-1.2.3.jar",
                "logback-core-1.2.3.jar",
                "mapstruct-1.0.0.Final.jar",
                "mybatis-3.5.10.jar",
                "mybatis-plus-3.5.3.1.jar",
                "mybatis-plus-annotation-3.5.3.1.jar",
                "mybatis-plus-boot-starter-3.5.3.1.jar",
                "mybatis-plus-core-3.5.3.1.jar",
                "mybatis-plus-extension-3.5.3.1.jar",
                "mybatis-spring-2.0.7.jar",
                "mysql-connector-java-8.0.15.jar",
                "protostuff-api-1.8.0.jar",
                "protostuff-collectionschema-1.8.0.jar",
                "protostuff-core-1.8.0.jar",
                "protostuff-runtime-1.8.0.jar",
                "scala-library-2.11.12.jar",
                "seatunnel-api-2.3.3.jar",
                "seatunnel-app-1.0.0.jar",
                "seatunnel-common-2.3.3.jar",
                "seatunnel-config-base-2.3.3.jar",
                "seatunnel-config-shade-2.3.3.jar",
                "seatunnel-core-starter-2.3.3.jar",
                "seatunnel-datasource-client-1.0.0.jar",
                "seatunnel-dynamicform-1.0.0.jar",
                "seatunnel-engine-client-2.3.3.jar",
                "seatunnel-engine-common-2.3.3.jar",
                "seatunnel-engine-core-2.3.3.jar",
                "seatunnel-guava-2.3.3-optional.jar",
                "seatunnel-jackson-2.3.3-optional.jar",
                "seatunnel-plugin-discovery-2.3.3.jar",
                "seatunnel-server-common-1.0.0.jar",
                "seatunnel-transforms-v2-2.3.3.jar",
                "serializer-api-2.3.3.jar",
                "serializer-protobuf-2.3.3.jar",
                "slf4j-api-1.7.25.jar",
                "snakeyaml-1.29.jar",
                "spring-aop-5.3.20.jar",
                "spring-beans-5.3.20.jar",
                "spring-boot-2.6.8.jar",
                "spring-boot-autoconfigure-2.6.8.jar",
                "spring-boot-starter-2.6.8.jar",
                "spring-boot-starter-aop-2.6.8.jar",
                "spring-boot-starter-jdbc-2.6.8.jar",
                "spring-boot-starter-jetty-2.6.8.jar",
                "spring-boot-starter-json-2.6.8.jar",
                "spring-boot-starter-web-2.6.8.jar",
                "spring-context-5.3.20.jar",
                "spring-core-5.3.20.jar",
                "spring-expression-5.3.20.jar",
                "spring-jcl-5.3.20.jar",
                "spring-jdbc-5.3.20.jar",
                "spring-plugin-core-1.2.0.RELEASE.jar",
                "spring-plugin-metadata-1.2.0.RELEASE.jar",
                "spring-tx-5.3.20.jar",
                "spring-web-5.3.20.jar",
                "spring-webmvc-5.3.20.jar",
                "springfox-core-2.6.1.jar",
                "springfox-schema-2.6.1.jar",
                "springfox-spi-2.6.1.jar",
                "springfox-spring-web-2.6.1.jar",
                "springfox-swagger-common-2.6.1.jar",
                "springfox-swagger-ui-2.6.1.jar",
                "springfox-swagger2-2.6.1.jar",
                "swagger-annotations-1.5.10.jar",
                "swagger-annotations-2.2.14.jar",
                "swagger-models-1.5.10.jar",
                "tomcat-embed-el-9.0.63.jar"
        ));
    }

}
