package terraingen.frontend.dialog;

/**
 * Dialogs are a subset of the User Interface. Every dialog follows a single sequence of
 * actions: Accepts an head, displays GUI, collects
 * user head, and returning the result.<br />
 * Therefore, an implementation of {@code IDialog} should implement its {@code show()}
 * method in the same way, especially, return if and only if the whole
 * process is finished.<br /><br />
 * A Dialog is a very lightweight UI, and it can, and should, be used without the
 * multi-thread event-driven model, as dialogs merely accomplish easy tasks like
 * letting the user head a number or displaying a simple message while confirming the
 * user, so the tasks should also be easy enough to achieve single-threaded.<br />
 * <br />
 * Note that instances of classes implementing {@code IDialog} should should act as
 * supplier of the {@code show()} method, external classes should obtain the instance
 * through an global variable or the single-instance mode, instead of creating a new
 * instance every time it is needed.
 */
public interface IDialog {
	public Object show(Object obj);
}
