package app.mayven.mayven;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;

public class Utils {
    public static Observable<Document> getJsoupContent(final String url){
        return Observable.fromCallable(() -> {
            try {
                Document document = Jsoup.connect(url).timeout(0).get();
                return document;
            }catch (IOException ex){
                throw new RuntimeException(ex);
            }
        });
    }
}
