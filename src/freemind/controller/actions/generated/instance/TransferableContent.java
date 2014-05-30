package freemind.controller.actions.generated.instance;
/* TransferableContent...*/
import java.util.ArrayList;
public class TransferableContent {
  protected String transferable;

  protected String transferableAsPlainText;

  protected String transferableAsRTF;

  protected String transferableAsDrop;

  protected String transferableAsHtml;

  protected String transferableAsImage;

  public String getTransferable() {
    return this.transferable;
  }

  public void setTransferable(String value){
    this.transferable = value;
  }

  public String getTransferableAsPlainText() {
    return this.transferableAsPlainText;
  }

  public void setTransferableAsPlainText(String value){
    this.transferableAsPlainText = value;
  }

  public String getTransferableAsRTF() {
    return this.transferableAsRTF;
  }

  public void setTransferableAsRTF(String value){
    this.transferableAsRTF = value;
  }

  public String getTransferableAsDrop() {
    return this.transferableAsDrop;
  }

  public void setTransferableAsDrop(String value){
    this.transferableAsDrop = value;
  }

  public String getTransferableAsHtml() {
    return this.transferableAsHtml;
  }

  public void setTransferableAsHtml(String value){
    this.transferableAsHtml = value;
  }

  public String getTransferableAsImage() {
    return this.transferableAsImage;
  }

  public void setTransferableAsImage(String value){
    this.transferableAsImage = value;
  }

  public void addTransferableFile(TransferableFile transferableFile) {
    transferableFileList.add(transferableFile);
  }

  public void addAtTransferableFile(int position, TransferableFile transferableFile) {
    transferableFileList.add(position, transferableFile);
  }

  public TransferableFile getTransferableFile(int index) {
    return (TransferableFile)transferableFileList.get( index );
  }

  public void removeFromTransferableFileElementAt(int index) {
    transferableFileList.remove( index );
  }

  public int sizeTransferableFileList() {
    return transferableFileList.size();
  }

  public void clearTransferableFileList() {
    transferableFileList.clear();
  }

  public java.util.List getListTransferableFileList() {
    return java.util.Collections.unmodifiableList(transferableFileList);
  }
    protected ArrayList transferableFileList = new ArrayList();

  public void addTransferableImage(TransferableImage transferableImage) {
    transferableImageList.add(transferableImage);
  }

  public void addAtTransferableImage(int position, TransferableImage transferableImage) {
    transferableImageList.add(position, transferableImage);
  }

  public TransferableImage getTransferableImage(int index) {
    return (TransferableImage)transferableImageList.get( index );
  }

  public void removeFromTransferableImageElementAt(int index) {
    transferableImageList.remove( index );
  }

  public int sizeTransferableImageList() {
    return transferableImageList.size();
  }

  public void clearTransferableImageList() {
    transferableImageList.clear();
  }

  public java.util.List getListTransferableImageList() {
    return java.util.Collections.unmodifiableList(transferableImageList);
  }
    protected ArrayList transferableImageList = new ArrayList();

} /* TransferableContent*/
