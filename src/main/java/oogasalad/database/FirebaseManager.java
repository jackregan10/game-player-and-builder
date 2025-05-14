package oogasalad.database;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import com.google.firebase.cloud.StorageClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import oogasalad.model.config.GameConfig;

/**
 * Initialize a Firebase database for the project
 *
 * @author Justin Aronwald
 */
public class FirebaseManager {

  private static Firestore db;
  private static final String BUCKET_NAME = "oogasalad-a908c.firebasestorage.app";
  private static final String FIREBASE_STRING_PREFIX = "levels/";


  /**
   * Initializes the Firebase app using the service JSON file This should only be called once during
   * application startup.
   *
   * @throws IOException - if the credential file cannot be read, throws error
   */
  public static void initializeFirebase() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      FileInputStream serviceAccount = new FileInputStream(
          "data/DatabaseInformation/oogasalad-a908c-firebase-adminsdk-fbsvc-73ed2b05e6.json");

      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setStorageBucket("oogasalad-a908c.firebasestorage.app")
          .build();

      FirebaseApp.initializeApp(options);
      db = FirestoreClient.getFirestore();
      LOGGER.info(GameConfig.getText("firebaseAlreadyExists"));
    } else {
      LOGGER.info(GameConfig.getText("firebaseAlreadyExists"));

    }
  }

  /**
   * Returns the Firestone database instance This should only be called after initialization has
   * been done
   *
   * @return - the initialized instance
   */
  public static Firestore getDB() {
    return db;
  }

  /**
   * Method that handles uploading a profile photo to the Firestore Storage
   *
   * @param pfpFile - the image file
   * @param username - the username that is storing the image
   * @return - the public URL to the image for the Database to store
   * @throws IOException - true if errors occur during parsing
   */
  public static String uploadPfp(String username, File pfpFile) throws IOException {
    String sanitizedFileName = sanitizePath(pfpFile.getName());
    String fileName = "profile_photos/" + username + "_" + sanitizedFileName;

    return getString(pfpFile, fileName);
  }

  private static String sanitizePath(String path) {
    return path.replaceAll("\\s+", "_");
  }


  /**
   * Had a little help from ChatGPT with parsing Folders for Firebase
   * Uploads a complete level folder
   * (including its JSON file and resources) to Firebase Storage
   * <p>
   * The uploaded files will be stored under the path:
   * {@code levels/userId/levelName/[relative path]}
   * <p>
   *
   * @param userId - the username of the user who owns the level
   * @param levelName - the name of the level being uploaded
   * @param levelFolder - the local folder containing the level.json and resources/ folder
   * @throws DatabaseException - if an error occurs during file upload or folder traversal
   */
  public static void uploadLevelToFirebase(String userId, String levelName, File levelFolder)
      throws DatabaseException {
    Path basePath = levelFolder.toPath();

    String sanitizedLevelName = sanitizePath(levelName);

    File[] contents = levelFolder.listFiles();
    if (contents != null && contents.length == 1 && contents[0].isDirectory()) {
      basePath = contents[0].toPath();  // Switch to the inner folder
    }

    try (Stream<Path> paths = Files.walk(basePath)) {
      for (Path file : (Iterable<Path>) paths.filter(Files::isRegularFile)::iterator) {

        String relativePath = basePath.relativize(file).toString().replace("\\", "/");
        String sanitizedRelativePath = sanitizePath(relativePath);
        String firebasePath = FIREBASE_STRING_PREFIX + userId + "/" + sanitizedLevelName + "/" + sanitizedRelativePath;
        uploadEachFile(file, firebasePath);
      }
    } catch (IOException e) {

      // TODO: Use GameConfig.getText()
      throw new DatabaseException("Error walking through level folder.", e);
    }
  }

  private static void uploadEachFile(Path file, String firebasePath) throws DatabaseException {
    try {
      uploadFileToFirebase(file.toFile(), firebasePath);
    } catch (IOException e) {
      // TODO: Use GameConfig.getText()
      throw new DatabaseException("Error uploading file: " + file.getFileName(), e);
    }
  }

  private static void uploadFileToFirebase(File file, String firebasePath) throws IOException {
    getString(file, firebasePath);
  }


  private static String getString(File jsonFile, String fileName) throws IOException {
    String contentType = Files.probeContentType(jsonFile.toPath());

    Bucket bucket = StorageClient.getInstance().bucket(BUCKET_NAME);
    Blob blob = bucket.get(fileName);
    if (blob != null) {
      blob.delete();
    }

    Blob newBlob = bucket.create(fileName, new FileInputStream(jsonFile), contentType);

    newBlob.createAcl(com.google.cloud.storage.Acl.of(
        com.google.cloud.storage.Acl.User.ofAllUsers(),
        com.google.cloud.storage.Acl.Role.READER
    ));

    return String.format("https://storage.googleapis.com/%s/%s", bucket.getName(), fileName);
  }

  /**
   * Method to delete any file from S3 Firebase blob storage
   *
   * @param userId - the username of the user
   * @param levelName - the name of the level
   * @return - true if successful
   */
  public static boolean deleteLevelFolder(String userId, String levelName) {
    String prefix = FIREBASE_STRING_PREFIX + userId + "/" + sanitizePath(levelName) + "/";
    Bucket bucket = StorageClient.getInstance().bucket();
    Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(prefix));

    boolean success = true;
    for (Blob blob : blobs.iterateAll()) {
      success &= blob.delete();
    }
    return success;
  }

  /**
   * Downloads an entire level folder from Firebase Storage, including the level JSON
   * and any associated resource files
   * <p>
   * The downloaded files are reconstructed locally with the same directory structure
   * as they were originally uploaded, under a new temporary folder
   * </p>
   *
   * @param userId - The username of the user who owns the level.
   * @param levelName - The name of the level to download
   * @return A File object representing the root of the reconstructed level folder
   * @throws IOException - If an error occurs during downloading or file writing
   */
  public static File downloadLevelFolder(String userId, String levelName) throws IOException {
    String prefix = FIREBASE_STRING_PREFIX + userId + "/" + levelName + "/";
    Bucket bucket = StorageClient.getInstance().bucket();
    Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(prefix));

    File localLevelFolder = Files.createTempDirectory(levelName).toFile();

    for (Blob blob : blobs.iterateAll()) {
      if (!blob.isDirectory()) {
        String relativePath = blob.getName().substring(prefix.length()); // remove "levels/user/level/" part
        File localFile = new File(localLevelFolder, relativePath);

        localFile.getParentFile().mkdirs();

        try (FileOutputStream outputStream = new FileOutputStream(localFile)) {
          blob.downloadTo(outputStream);
        }
      }
    }

    return localLevelFolder;
  }

}