package server;

public class ServerMain {

    public static void main(String[] args) {

        // 1) UI 생성
        ServerWindow window = new ServerWindow();

        // 2) 게임룸 생성 (모든 클라이언트 공유)
        GameRoom gameRoom = new GameRoom();

        // 3) 서버 수신 스레드 시작 (accept 담당)
        ServerHandler serverHandler = new ServerHandler(54321, gameRoom, window);
        serverHandler.start();
    }
}
