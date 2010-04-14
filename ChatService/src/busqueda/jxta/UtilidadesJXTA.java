
package busqueda.jxta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public abstract class UtilidadesJXTA {
    public static final String ATRIBUTO_BUSQUEDA = "Name";
    public static final long PIPE_TIMEOUT = 5 * 1000; // 5 segundos
    public static final int DELAY_BUSQUEDA = 10 * 1000; // 10 segundos

    public static PipeAdvertisement crearPipeAdvertisementFromString(String advString) {
        try {
            ByteArrayInputStream advStream = new ByteArrayInputStream(advString.getBytes());
            XMLDocument xml = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, advStream);
            PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(xml);
            return advertisement;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el PipeAdvertisement.");
            return null;
        }
    }

    public static PipeAdvertisement crearPipeAdvertisement(PeerGroup grupo, String nombre, String descripcion) {
        PipeID pipeID = (PipeID) IDFactory.newPipeID(grupo.getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(nombre);
        advertisement.setDescription(descripcion);
        return advertisement;
    }

    public static InputPipe crearInputPipe(PeerGroup grupo, PipeAdvertisement advertisement) {
        try {
            InputPipe pipe = grupo.getPipeService().createInputPipe(advertisement);
            return pipe;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el InputPipe.");
            return null;
        }
    }

    public static InputPipe crearInputPipe(PeerGroup grupo, PipeAdvertisement advertisement, PipeMsgListener listener) {
        try {
            InputPipe pipe = grupo.getPipeService().createInputPipe(advertisement, listener);
            return pipe;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el InputPipe.");
            return null;
        }
    }

    public static OutputPipe crearOuputPipe(PeerGroup grupo, PipeAdvertisement advertisement) {
        try {
            OutputPipe pipe = grupo.getPipeService().createOutputPipe(advertisement, PIPE_TIMEOUT);
            return pipe;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el OutputPipe.");
            return null;
        }
    }

    public static void publicarAdvertisement(PeerGroup grupo, Advertisement advertisement) {
        try {
            grupo.getDiscoveryService().publish(advertisement);
            grupo.getDiscoveryService().remotePublish(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo publicar el advertisement.");
        }
    }

    public static void publicarAdvertisementLocal(PeerGroup grupo, Advertisement advertisement) {
        try {
            grupo.getDiscoveryService().publish(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo publicar el advertisement.");
        }
    }

    public static void eliminarAdvertisement(PeerGroup grupo, Advertisement advertisement) {
        try {
            grupo.getDiscoveryService().flushAdvertisement(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo eliminar el advertisement.");
        }
    }

}
