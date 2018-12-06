/**
 *Copyright (c) 2014 groupN カードプロジェクト
 * Licensed under the Apache License, Version 2.0 (the “Lisence”);
 * you may not use this file except in compliance with the License.
 * you may obtaion a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.1
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ac.shohoku.pico.speedcard;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * @author 西山彩花
 *
 */
public class SpeedCardView extends SurfaceView implements Runnable, Callback {
    public static final int LV1_DISP = 1;//レベル1スタート表示
    public static final int LV1_PLAY = 2; //レベル1プレイ中
    public static final int LV2_DISP = 3;   //レベル2スタート表示
    public static final int LV2_PLAY = 4; //レベル2プレイ中
    public static final int LV3_DISP = 5;//レベル3スタート表示
    public static final int LV3_PLAY = 6; //レベル3プレイ中
    public static final int LV4_DISP = 7;   //レベル4スタート表示
    public static final int LV4_PLAY = 8; //レベル4プレイ中
    public static final int GAME_OVER = 100;//ゲームオーバー
    public static final int GAME_CLEAR = 101; //ゲームクリア

    public static int NEUX7_WIDTH = 800;
    public static int NEUX7_HEIGHT = 1280;

    private SurfaceHolder mHolder;
    private int mGameState; // ゲームの状態を表す変数
    private long mLvStart, mLvTime; // レベルの開始と時間

    /**
     * コンストラクタ<br />
     * 引数はContextとAttributeSet*
     *
     * @paramcontext
     * @paramattrs
     */
    public SpeedCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初期化用のメソッド<br />
     * 各種変数の初期化やコールバックの割り当てなどを行う
     */
    private void init() {

        mLvStart = System.currentTimeMillis();
        mLvStart = System.currentTimeMillis();
        mHolder = getHolder(); // SurfaceHolderを取得する．
        mHolder.addCallback(this);
        setFocusable(true); // フォーカスをあてることを可能にするメソッド
        requestFocus(); // フォーカスを要求して実行を可能にする
        mGameState = LV1_DISP; //最初はレベル1表示画面
    }

    /**
     * 定期的に実行するスレッドを生成し，定期的に実行の設定を行う<br />
     * このメソッドはサーフェスが生成されたタイミングで実行される．
     */
    private void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//scheduledAtFixedRateの第1引数：実行可能なクラス．第4引数：ミリ秒に設定している
//第2引数は実行を開始する時刻，第3引数は実行する間隔：
        executor.scheduleAtFixedRate(this, 30, 30, TimeUnit.MILLISECONDS);
    }

    /*
     * @see
     * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /*
     * サーフェスが生成されたとき，とりあえず画面に表示し，その後定期実行するスレッドをスタート
     *
     * @see
     * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    public void surfaceCreated(SurfaceHolder holder) {
        draw();
        start();
    }

    /*
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * イベント処理するためのメソッド
     *
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {    //イベントの種類によって処理を振り分ける
            case MotionEvent.ACTION_DOWN:    //画面上で押下されたとき
                switch (mGameState) {    //ゲームの状態によって処理を振り分ける
                    case LV1_DISP:
                        mGameState = LV1_PLAY;
                        break;
                    case LV1_PLAY:
                        mGameState = LV2_DISP;
                        mLvStart = System.currentTimeMillis();
                        break;
                    case LV2_DISP:
                        mGameState = LV2_PLAY;
                        break;
                    case LV2_PLAY:
                        mGameState = LV3_DISP;
                        mLvStart = System.currentTimeMillis();
                        break;
                    case LV3_DISP:
                        mGameState = LV3_PLAY;
                        break;
                    case LV3_PLAY:
                        mGameState = LV4_DISP;
                        mLvStart = System.currentTimeMillis();
                        break;
                    case LV4_DISP:
                        mGameState = LV4_PLAY;
                        break;
                    case LV4_PLAY:
                        mGameState = GAME_OVER;
                        break;
                    case GAME_OVER:
                        mGameState = GAME_CLEAR;
                        break;
                    case GAME_CLEAR:
                        mGameState = LV1_DISP;
                        break;
                }
                break;
        }
        return true;
    }

    /**
     * 描画用のメソッド<br />
     * 画面への描画処理はすべてこの中に書く
     */

    public void countDown (Canvas canvas){
        Paint paint = new Paint();
        paint.setTextSize(100);
        canvas.drawText("" + ((3000 - mLvTime) / 1000 + 1), 190, 527, paint);  //画面の中心付近に
    }
    private void draw() {
        Canvas canvas = mHolder.lockCanvas(); // サーフェースをロック
        canvas.drawColor(Color.WHITE); // キャンバスを白に塗る

        Paint paint = new Paint();
        paint.setTextSize(30);
        switch (mGameState) {    //ゲームの状態によって処理を振り分ける
            case LV1_DISP:
//LV1_DISPの時の描画処理
                String msg = "LEVEL1 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV1_PLAY:
//LV1_PLAYの時の描画処理
                msg = "LEVEL1 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                break;
            case LV2_DISP:
//LV2_DISPの時の描画処理
                msg = "LEVEL2 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV2_PLAY:
//LV2_PLAYの時の描画処理
                msg = "LEVEL2 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                break;
            case LV3_DISP:
//LV3_DISP時の描画処理
                msg = "LEVEL3 DISP";
                canvas.drawText(msg, 10, 50, paint);
                break;
            case LV3_PLAY:
//LV3_PLAYの時の描画処理
                msg = "LEVEL3 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV4_DISP:
//LV4_DISPの時の描画処理
                msg = "LEVEL4 DISP";
                canvas.drawText(msg, 10, 50, paint);
                countDown(canvas);
                break;
            case LV4_PLAY:
//LV4_PLAYの時の描画処理
                msg = "LEVEL4 PLAY";
                canvas.drawText(msg, 10, 50, paint);
                break;
            case GAME_OVER:
//GAME_OVER時の描画処理
                msg = "GAME OVER";
                canvas.drawText(msg, 10, 50, paint);
                break;
            case GAME_CLEAR:
//GAME_CLEARの時の描画処理
                msg = "GAME CLEAR";
                canvas.drawText(msg, 10, 50, paint);
                break;
        }
        mHolder.unlockCanvasAndPost(canvas); // サーフェースのロックを外す
    }

    /*
     * 実行可能メソッド．このクラスの中では定期実行される
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {

        draw();
        mLvTime = System.currentTimeMillis() - mLvStart;

        if (mLvTime >= 3000) { //３秒経過したら状態を変更する
            switch (mGameState) {
                case LV1_DISP:
                    mGameState = LV1_PLAY;
                    break;
                case LV2_DISP:
                    mGameState = LV2_PLAY;
                    break;
                case LV3_DISP:
                    mGameState = LV3_PLAY;
                    break;
                case LV4_DISP:
                    mGameState = LV4_PLAY;
                    break;
            }
        }
        mLvTime = System.currentTimeMillis() - mLvStart;

        if (mLvTime >= 3000) {
            switch (mGameState) {
                case LV1_DISP:
                    mGameState = LV1_PLAY;
                    break;
                case LV2_DISP:
                    mGameState = LV2_PLAY;
                    break;
                case LV3_DISP:
                    mGameState = LV3_PLAY;
                    break;
                case LV4_DISP:
                    mGameState = LV4_PLAY;
                    break;
            }
        }


    }
}




