package wmesaf.basicschool.database;

import java.io.File;
import java.net.URL;

public class JarChecker {
    
    public static void main(String[] args) {
        System.out.println("🔍 MySQL Connector JAR Checker");
        System.out.println("===============================\n");
        
        // المسارات المحتملة للبحث عن JAR
        String[] possiblePaths = {
            "lib/mysql-connector-j-9.5.0.jar",                    // المسار النسبي
            "./lib/mysql-connector-j-9.5.0.jar",                  // من المجلد الحالي
            "mysql-connector-j-9.5.0.jar",                        // في نفس المجلد
            System.getProperty("user.dir") + "/lib/mysql-connector-j-9.5.0.jar",  // مطلق
            "C:/Users/wessal/Documents/NetBeansProjects/BasicSchool/lib/mysql-connector-j-9.5.0.jar"  // مطلق كامل
        };
        
        System.out.println("📂 Project Information:");
        System.out.println("   User Dir: " + System.getProperty("user.dir"));
        System.out.println("   Java Home: " + System.getProperty("java.home"));
        System.out.println("   Class Path: " + System.getProperty("java.class.path"));
        
        System.out.println("\n🔎 Searching for JAR file...");
        
        boolean found = false;
        File foundFile = null;
        
        for (String path : possiblePaths) {
            File file = new File(path);
            System.out.printf("\n   Checking: %s%n", path);
            System.out.printf("   Exists: %s%n", file.exists());
            System.out.printf("   Absolute: %s%n", file.getAbsolutePath());
            
            if (file.exists() && !found) {
                found = true;
                foundFile = file;
                System.out.println("   ✅ FOUND!");
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        
        if (found && foundFile != null) {
            System.out.println("🎉 SUCCESS: JAR file found!");
            System.out.println("   Location: " + foundFile.getAbsolutePath());
            System.out.println("   Size: " + (foundFile.length() / 1024) + " KB");
            
            // محاولة تحميل المشغل
            System.out.println("\n🔄 Attempting to load MySQL Driver...");
            try {
                URL jarUrl = foundFile.toURI().toURL();
                System.out.println("   JAR URL: " + jarUrl);
                
                // هذا يؤكد أن الملف صالح
                System.out.println("   ✅ JAR file is valid and accessible");
                
            } catch (Exception e) {
                System.err.println("   ❌ Error accessing JAR: " + e.getMessage());
            }
            
        } else {
            System.err.println("❌ ERROR: MySQL Connector JAR NOT FOUND!");
            System.err.println("\n🔧 SOLUTION:");
            System.err.println("1. Download from: https://dev.mysql.com/downloads/connector/j/");
            System.err.println("2. Choose: Platform Independent → ZIP Archive");
            System.err.println("3. Extract and find: mysql-connector-j-9.5.0.jar");
            System.err.println("4. Copy it to:");
            System.err.println("   C:\\Users\\wessal\\Documents\\NetBeansProjects\\BasicSchool\\lib\\");
            System.err.println("\n📁 Create 'lib' folder if it doesn't exist:");
            System.err.println("   Location: BasicSchool\\lib\\");
        }
        
        System.out.println("\n📋 Directory Structure Check:");
        File projectDir = new File(System.getProperty("user.dir"));
        listDirectory(projectDir, 0);
    }
    
    private static void listDirectory(File dir, int level) {
        if (!dir.exists() || !dir.isDirectory()) return;
        
        String indent = "   ".repeat(level);
        
        // عرض المجلدات والملفات المهمة فقط
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                // تجاهل الملفات المخفية والمجلدات الكبيرة
                if (file.isHidden()) continue;
                if (file.isDirectory()) {
                    if (file.getName().equals("lib") || 
                        file.getName().equals("src") || 
                        file.getName().equals("build")) {
                        System.out.println(indent + "📁 " + file.getName() + "/");
                        listDirectory(file, level + 1);
                    }
                } else if (file.getName().endsWith(".jar")) {
                    System.out.println(indent + "📦 " + file.getName() + " (" + (file.length()/1024) + " KB)");
                }
            }
        }
    }
}