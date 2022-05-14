package graphic;

public interface IViewable {
    void NotifyViews();
    void AddView(View view);
    void RemoveView(View view);
}
