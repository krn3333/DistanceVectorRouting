import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RouterListener extends Thread {

        ServerSocket ss;
        Router router;
        RouterListener(Router router)
        {
                this.router=router;
                try {
                        ss= new ServerSocket(5555);
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
        public void run()
        {
                while(true)
                {
                                          try {

                                Socket s= ss.accept();
                                ObjectInputStream ois= new ObjectInputStream(s.getInputStream());

                                Object data= ois.readObject();

                                        router.update(data,s.getInetAddress().getHostAddress());

                        } catch (IOException e) {
                                // TODO Auto-generated catch block

                        } catch (ClassNotFoundException e) {
                                // TODO Auto-generated catch block

                        }

                }
        }
}
                                        
