package oogasalad.model.service;

import static oogasalad.model.config.GameConfig.getText;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;

/**
 * Abstract class that defines a lot of the Firestore database methods

 */
public abstract class FirestoreService {

  private final String collectionName;

  /**
   * Creates a new instance of the Firestore service with the collection name
   *
   * @param collectionName - the name of Firestore collection
   */
  protected FirestoreService(String collectionName) {
    this.collectionName = collectionName;
  }

  /**
   * Returns a Firestore CollectionReference for a given name (the table pretty much)
   *
   * @return - the reference to the collection
   */
  protected CollectionReference getCollection() {
    return FirebaseManager.getDB().collection(collectionName);
  }

  /**
   * Returns a Firestore DocumentReference for the given document ID (in Table terms, a reference to
   * a row)
   *
   * @param id      the document ID
   * @return DocumentReference - a specific row reference in the firestone database
   */
  protected DocumentReference getDocRef(String id) {
    return getCollection().document(id);
  }

  /**
   * Retrieves the DocumentSnapshot for the given document ID (the query result row)
   *
   * @param id - the document ID
   * @return the DocumentSnapshot object
   * @throws DatabaseException if the document could not be fetched
   */
  protected boolean documentExists(String id) throws DatabaseException {
    return getDocument(id).exists();
  }

  protected DocumentSnapshot getDocument(String id) throws DatabaseException {
    try {
      return getDocRef(id).get().get();
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(GameConfig.getText("failedToFetch", id), e);
    }
  }

  /**
   * Adds a document with the given ID and data to the given collection
   *
   * @param id      the document ID (username)
   * @param data            the data to write
   * @throws DatabaseException if the document could not be written
   */
  protected void saveToDatabase(String id, Map<String, Object> data) throws DatabaseException {
    try {
      getDocRef(id).set(data).get();
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(getText("databaseAddError", id), e);
    }
  }

  /**
   * Deletes a document with the given ID from the collection provided
   *
   * @param id      the document ID to delete
   * @throws DatabaseException if the document could not be deleted
   */
  protected void deleteFromDatabase(String id) throws DatabaseException {
    try {
      getDocRef(id).delete().get();
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(getText("databaseDeleteError", id), e);
    }
  }

  /**
   * Update current document information with new information
   *
   * @param documentId - the unique id of the document
   * @param data - the data being updated
   * @throws DatabaseException - true if there's an error fetching data
   */
  protected void updateDatabase(String documentId,
      Map<String, Object> data) throws DatabaseException {
    try {
      getDocRef(documentId).update(data).get();
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(getText("databaseUpdateError", documentId), e);
    }
  }
}