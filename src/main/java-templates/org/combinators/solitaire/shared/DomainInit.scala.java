@(ModelInit: Seq[Statement], ViewInit: Seq[Statement], ControlInit : Seq[Statement], SetupInitialState : Seq[Statement])

// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

@Java(ModelInit)

@Java(ViewInit)

@Java(ControlInit)

@Java(SetupInitialState)