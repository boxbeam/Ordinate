package redempt.ordinate.brigadier;

public interface BrigadierAdapter<C> {

	public void convert(C component, BrigadierBuilder<?> builder);
	
}
