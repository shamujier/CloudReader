package com.example.jingbin.cloudreader.viewmodel.movie;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jingbin.cloudreader.app.App;
import com.example.jingbin.cloudreader.base.BaseViewModel;
import com.example.jingbin.cloudreader.bean.ComingFilmBean;
import com.example.jingbin.cloudreader.bean.MtimeFilmeBean;
import com.example.jingbin.cloudreader.bean.book.BookBean;
import com.example.jingbin.cloudreader.http.HttpClient;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @author jingbin
 * @data 2019/05/14
 */

public class FilmViewModel extends BaseViewModel {

    // 开始请求的角标
    private int mStart = 0;
    // 一次请求的数量
    private int mCount = 18;

    public final ObservableField<String> bookType = new ObservableField<>();

    public FilmViewModel(@NonNull Application application) {
        super(application);
    }

    public void setStart(int mStart) {
        this.mStart = mStart;
    }

    public int getStart() {
        return mStart;
    }

    public MutableLiveData<BookBean> getBook() {
        final MutableLiveData<BookBean> data = new MutableLiveData<>();
        Disposable subscribe = HttpClient.Builder.getDouBanService().getBook(bookType.get(), mStart, mCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BookBean>() {
                    @Override
                    public void accept(BookBean bookBean) throws Exception {
                        data.setValue(bookBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        data.setValue(null);
                    }
                });
        addDisposable(subscribe);
        return data;
    }

    public void handleNextStart() {
        mStart += mCount;
    }

    private static final String TAG = "FilmViewModel";
    public MutableLiveData<MtimeFilmeBean> getHotFilm() {
        final MutableLiveData<MtimeFilmeBean> data = new MutableLiveData<>();
        Disposable subscribe = HttpClient.Builder.getMtimeServer().getHotFilm()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MtimeFilmeBean>() {
                    @Override
                    public void accept(MtimeFilmeBean bookBean) throws Exception {
//                        Log.i(TAG, "accept: ----------------------------");
                        data.setValue(bookBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        Log.i(TAG, "accept: ===============================");
                        String json=getJson("MovieComingNew.json", App.getInstance());
                        MtimeFilmeBean bookBean = new Gson().fromJson(json, MtimeFilmeBean.class);
                        data.setValue(bookBean);
                    }
                });
        addDisposable(subscribe);
        return data;
    }

    public static String getJson(String fileName, Context context){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line=bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public MutableLiveData<ComingFilmBean> getComingFilm() {
        final MutableLiveData<ComingFilmBean> data = new MutableLiveData<>();
        Disposable subscribe = HttpClient.Builder.getMtimeServer().getComingFilm()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ComingFilmBean>() {
                    @Override
                    public void accept(ComingFilmBean bookBean) throws Exception {
//                        Log.i(TAG, "accept: -------------------------------");
                        data.setValue(bookBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        Log.i(TAG, "accept: ===============================");
//                        data.setValue(null);
                        String json=getJson("MovieComingNew.json", App.getInstance());
                        ComingFilmBean bookBean = new Gson().fromJson(json, ComingFilmBean.class);
                        data.setValue(bookBean);
                    }
                });
        addDisposable(subscribe);
        return data;
    }
}
