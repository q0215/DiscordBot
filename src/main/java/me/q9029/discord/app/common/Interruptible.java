package me.q9029.discord.app.common;

/**
 * このインタフェースを実装するクラスは処理の中断が可能である。
 * 
 * @author q9029
 */
public interface Interruptible {

	/**
	 * このメソッドが実行された場合、処理を中断する。
	 */
	void interrupt();
}
