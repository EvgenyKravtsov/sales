package kgk.mobile.presentation.view.base;


public abstract class BasePresenterImpl<T extends BaseView> implements BasePresenter<T> {

    protected T view;

    //// BASE PRESENTER

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
