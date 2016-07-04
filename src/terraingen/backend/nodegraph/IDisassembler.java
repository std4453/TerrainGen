package terraingen.backend.nodegraph;

import java.util.List;

/**
 * Disassembles an input of class I into multiple outputs of class O
 */
public interface IDisassembler<I, O> {
	List<O> disassemble(I input);
}
