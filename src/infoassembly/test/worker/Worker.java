package infoassembly.test.worker;

import infoassembly.test.common.Info;

import java.util.List;
import java.util.Properties;

import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Worker implements GearmanFunction {

	private StanfordCoreNLP pipeline;

	public Worker() {
		initCoreNLP();
	}
	public static void main(String[] args) {

		Gearman gearman = Gearman.createGearman();

		GearmanServer server = gearman.createGearmanServer(Info.HOST, Info.PORT);

		GearmanWorker worker = gearman.createGearmanWorker();

		worker.addFunction(Info.FUNCTION_NAME, new Worker());

		worker.addServer(server);
		System.out.println("Worker Start success**********");
	}

	private void initCoreNLP() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		pipeline = new StanfordCoreNLP(props);
	}

	@Override
	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception {
//		System.out.println("Work call");
		String result = performTask(new String(data));
		return result.getBytes();
	}

	private String performTask(String text) {
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		StringBuffer _buffer = new StringBuffer("NER: ");
		int count = 0;

		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

				String word = token.get(TextAnnotation.class);
				String ne = token.get(NamedEntityTagAnnotation.class);

				if (!ne.equalsIgnoreCase("o") && _buffer.indexOf(word) == -1) {
					count++;
					_buffer.append("\"" + word + "\",");
				}
			}
		}
		_buffer.insert(0, "Number of NERs=" + count + " ");
		return _buffer.toString();

	}
}
