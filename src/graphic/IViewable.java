package graphic;

public interface IViewable {
    public void NotifyViews();
    public void AddView(View view);
    public void RemoveView(View view);
}
