package wmesaf.basicschool.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvancedJarChecker {
    public static void main(String[] args) {
        System.out.println("🔍 Advanced JAR Finder");
        System.out.println("======================\n");
        
        List<File> possibleLocations = new ArrayList<>();
        
        // 1. المسار الحالي ومجلدات المشروع
        String projectPath = System.getProperty("user.dir");
        possibleLocations.add(new File(projectPath + "/lib/mysql-connector-j-9.5.0.jar"));
        possibleLocations.add(new File(projectPath + "/mysql-connector-j-9.5.0.jar"));
        
        // 2. مجلد Downloads
        String home = System.getProperty("user.home");
        possibleLocations.add(new File(home + "/Downloads/mysql-connector-j-9.5.0.jar"));
        possibleLocations.add(new File(home + "/Downloads/mysql-connector-j-9.5.0.zip"));
        
        // 3. مكان فك الضغط
        possibleLocations.add(new File(home + "/Downloads/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar"));
        
        // 4. مجلد Desktop
        possibleLocations.add(new File(home + "/Desktop/mysql-connector-j-9.5.0.jar"));
        
        System.out.println("Project: " + projectPath);
        System.out.println("\nSearching in common locations...");
        
        boolean found = false;
        for (File location : possibleLocations) {
            System.out.printf("\n📍 %s%n", location.getAbsolutePath());
            if (location.exists()) {
                System.out.printf("   ✅ EXISTS! Size: %d KB%n", location.length() / 1024);
                System.out.printf("   📂 Parent: %s%n", location.getParent());
                
                if (location.getName().endsWith(".jar")) {
                    System.out.println("   🎯 This is a JAR file - READY TO USE!");
                    copyInstructions(location, projectPath);
                    found = true;
                    break;
                } else if (location.getName().endsWith(".zip")) {
                    System.out.println("   ⚠️ This is a ZIP file - need to extract first!");
                }
            } else {
                System.out.println("   ❌ Not found");
            }
        }
        
        if (!found) {
            System.err.println("\n❌ No JAR file found!");
            System.err.println("\n🚀 QUICK FIX - Add to Maven pom.xml:");
            System.err.println("""
                <dependency>
                    <groupId>com.mysql</groupId>
                    <artifactId>mysql-connector-j</artifactId>
                    <version>9.5.0</version>
                </dependency>
                """);
        }
    }
    
    private static void copyInstructions(File source, String projectPath) {
        System.out.println("\n📋 COPY INSTRUCTIONS:");
        System.out.println("1. Open File Explorer");
        System.out.println("2. Go to: " + source.getAbsolutePath());
        System.out.println("3. Copy the file");
        System.out.println("4. Go to: " + projectPath + "\\lib\\");
        System.out.println("5. Paste the file here");
        System.out.println("\nOr use this command in CMD:");
        System.out.printf("copy \"%s\" \"%s\\lib\\\"%n", 
            source.getAbsolutePath(), projectPath);
    }
}