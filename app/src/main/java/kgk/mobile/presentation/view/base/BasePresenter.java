package kgk.mobile.presentation.view.base;


public interface BasePresenter<V extends BaseView> {

    void attachView(V view);

    void detachView();
}
