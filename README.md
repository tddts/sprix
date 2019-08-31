# [sprix]
JavaFX Spring integration library.

## Features
- Simple JavaFX and Spring integration.
- JavaFX dialog management.
- Useful annotations.

## Usage
### Add SpriX into Spring application.
XML configuration:
```xml
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       ...
       xmlns:sprix="http://www.github.com/tddts/sprix"
       ...
       http://www.github.com/tddts/sprix http://www.github.com/tddts/sprix/sprix.xsd">
       
  <sprix:view file="fxml/main.fxml"
              starterClass="com.example.App"
              messageSource="messageSource"
              width="1024"
              height="600"
              title="Window title"/>
```
Annotation configuration TBA.
### Working with JavaFX controllers.
```java
// Controller is now a Spring bean.
@SprixController
public class SomeController {

  // Standard JavaFX annotation
  @FXML
  private Button someButton;
  
  // Inject message from resource bundle
  @Message("some.message.property.name")
  private String someMessage;
  
  // Inject text file content
  @LoadContent("/some_text.txt")
  private String someText;

  // Autowire Spring beans
  @Autowired
  private SomeService someService;

  // Do some post-constructing.
  @PostConstruct
  private void init() {
  }
```

### Managing JavaFX dialogs.
```java
// Load dialog from 'res/fxml/credentials-dialog.fxml'
// Cache dialog in memory
// Dialog is a Spring bean too
@SprixDialog(value = "fxml/credentials-dialog.fxml", cached = true)
public class CredentialsDialog extends Dialog<Pair<String, String>> {

  @FXML
  private TextField clientIdField;
  @FXML
  private PasswordField secretKeyField;

  @Message("msg.cancel")
  private String cancelMessage;

  public CredentialsDialog() {
  }

  @PostConstruct
  public void init() {
    ButtonType cancelButtonType = new ButtonType(cancelMessage, ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().addAll(ButtonType.OK, cancelButtonType);
    setResultConverter(this::getResult);
  }

  private Pair<String, String> getResult(ButtonType buttonType) {
    if (buttonType == ButtonType.OK) {
      return Pair.of(clientIdField.getText(), secretKeyField.getText());
    }
    return null;
  }
}
```
To use this dialog:
```java
  @Autowired
  private SprixDialogProvider dialogProvider;

  private Optional<Pair<String, String>> getCredentials() {
    return dialogProvider.getDialog(DevCredentialsDialog.class).showAndWait();
  }  
```
