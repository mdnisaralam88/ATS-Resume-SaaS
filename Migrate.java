import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Migrate {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting Migration Script...");
        
        File entityDir = new File("backend/src/main/java/com/resumeiq/entity");
        if (entityDir.exists() && entityDir.isDirectory()) {
            for (File f : entityDir.listFiles()) {
                if (f.getName().endsWith(".java")) {
                    processEntity(f);
                }
            }
        }
        
        File repoDir = new File("backend/src/main/java/com/resumeiq/repository");
        if (repoDir.exists() && repoDir.isDirectory()) {
            for (File f : repoDir.listFiles()) {
                if (f.getName().endsWith(".java")) {
                    processRepo(f);
                }
            }
        }
        System.out.println("Done.");
    }

    private static void processEntity(File file) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        
        // Imports
        content = content.replaceAll("import jakarta\\.persistence\\.\\*;", 
                "import org.springframework.data.annotation.Id;\n" +
                "import org.springframework.data.mongodb.core.mapping.Document;\n" +
                "import org.springframework.data.mongodb.core.mapping.DBRef;\n" +
                "import org.springframework.data.annotation.CreatedDate;\n" +
                "import org.springframework.data.annotation.LastModifiedDate;");
        content = content.replaceAll("import org\\.hibernate\\.annotations\\.(CreationTimestamp|UpdateTimestamp);", "");
        
        // Annotations
        content = content.replaceAll("@Entity", "@Document");
        content = content.replaceAll("@Table\\(.*?\\)", "");
        content = content.replaceAll("@Column\\(.*?\\)", "");
        content = content.replaceAll("@GeneratedValue\\(.*?\\)", "");
        content = content.replaceAll("@OneToMany\\(.*?\\)", "@DBRef(lazy = true)");
        content = content.replaceAll("@ManyToOne\\(.*?\\)", "@DBRef(lazy = true)");
        content = content.replaceAll("@OneToOne\\(.*?\\)", "@DBRef(lazy = true)");
        content = content.replaceAll("@JoinColumn\\(.*?\\)", "");
        content = content.replaceAll("@CreationTimestamp", "@CreatedDate");
        content = content.replaceAll("@UpdateTimestamp", "@LastModifiedDate");
        content = content.replaceAll("private Long id;", "private String id;");

        // Some manual cleanups
        content = content.replaceAll("(?m)^\\s*$\\n", ""); // remove empty lines for cleaner look

        Files.write(Paths.get(file.getAbsolutePath()), content.getBytes());
        System.out.println("Migrated Entity: " + file.getName());
    }
    
    private static void processRepo(File file) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        
        content = content.replaceAll("import org\\.springframework\\.data\\.jpa\\.repository\\.JpaRepository;", 
                "import org.springframework.data.mongodb.repository.MongoRepository;\n" +
                "import org.springframework.data.mongodb.repository.Query;\n" +
                "import org.springframework.data.mongodb.repository.Aggregation;");
        content = content.replaceAll("JpaRepository<([a-zA-Z]+),\\s*Long>", "MongoRepository<$1, String>");
        
        Files.write(Paths.get(file.getAbsolutePath()), content.getBytes());
        System.out.println("Migrated Repo: " + file.getName());
    }
}
