package freemind.controller.actions.generated.instance;
/* PasteNodeAction...*/
public class PasteNodeAction extends NodeAction {
  protected boolean isLeft;
  protected boolean asSibling;
  public boolean getIsLeft(){
    return isLeft;
  }
  public boolean getAsSibling(){
    return asSibling;
  }
  public void setIsLeft(boolean value){
    this.isLeft = value;
  }
  public void setAsSibling(boolean value){
    this.asSibling = value;
  }
  protected TransferableContent transferableContent;

  public TransferableContent getTransferableContent() {
    return this.transferableContent;
  }

  public void setTransferableContent(TransferableContent value){
    this.transferableContent = value;
  }

} /* PasteNodeAction*/
