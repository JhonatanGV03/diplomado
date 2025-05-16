package crypto;

import java.io.Serializable;

public class TransferProtocol implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TransferType {
        FILE_TRANSFER_REQUEST,
        FILE_DATA,
        KEY_DATA,
        HASH_DATA,
        TRANSFER_COMPLETE,
        INTEGRITY_CHECK_RESULT
    }

    private TransferType type;
    private String fileName;
    private byte[] data;
    private long fileSize;
    private boolean integrityCheckResult;

    // Constructor para solicitud de transferencia
    public TransferProtocol(TransferType type, String fileName, long fileSize) {
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // Constructor para datos
    public TransferProtocol(TransferType type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    // Constructor para resultado de verificación de integridad
    public TransferProtocol(TransferType type, boolean integrityCheckResult) {
        this.type = type;
        this.integrityCheckResult = integrityCheckResult;
    }

    // Getters y setters
    public TransferType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public long getFileSize() {
        return fileSize;
    }

    public boolean isIntegrityCheckResult() {
        return integrityCheckResult;
    }
}

