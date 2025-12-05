//package wmesaf.basicschool.database;
//
//public class TestDatabaseConnection {
//    public static void main(String[] args) {
//        System.out.println("=== Testing Database Connection ===\n");
//        
//        DatabaseConnection db = DatabaseConnection.getInstance();
//        
//        if (db.isConnectionValid()) {
//            System.out.println("✅ SUCCESS: Connected to " + db.getDatabaseType() + " database!");
//            System.out.println("   Database is ready for use");
//        } else {
//            System.out.println("❌ FAILED: Database connection failed!");
//            System.out.println("   Please check your database setup");
//        }
//        
//        // Test some queries
//        try {
//            System.out.println("\n🔍 Testing basic queries...");
//            var result = db.executeQuery("SELECT COUNT(*) as table_count FROM information_schema.tables WHERE table_schema = DATABASE()");
//            if (result.next()) {
//                System.out.println("✅ Tables in database: " + result.getInt("table_count"));
//            }
//        } catch (Exception e) {
//            System.out.println("⚠️ Query test skipped: " + e.getMessage());
//        }
//        
//        db.closeConnection();
//    }
//}