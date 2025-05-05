package nikita.math.solver.integrate;

import java.util.List;

public interface Multimodal {
	public void setMode(String shorthand);

	public void setMode(IntegratorMode mode);

	public IntegratorMode getMode();

	public List<IntegratorMode> getModes();
}
