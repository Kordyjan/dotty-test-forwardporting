package dotty.tools.dotc.reporting.diagnostic

/** Unique IDs identifying the messages */
enum ErrorMessageID extends java.lang.Enum[ErrorMessageID] {

  // IMPORTANT: Add new IDs only at the end and never remove IDs
  case
    LazyErrorId, // // errorNumber: -2
    NoExplanationID, // errorNumber: -1

    EmptyCatchOrFinallyBlockID, // errorNumber: 0
    EmptyCatchBlockID, // errorNumber: 1
    EmptyCatchAndFinallyBlockID, // errorNumber: 2
    DeprecatedWithOperatorID,
    CaseClassMissingParamListID,
    DuplicateBindID,
    MissingIdentID,
    TypeMismatchID,
    NotAMemberID,
    EarlyDefinitionsNotSupportedID,
    TopLevelImplicitClassID,
    ImplicitCaseClassID,
    ImplicitClassPrimaryConstructorArityID,
    ObjectMayNotHaveSelfTypeID,
    TupleTooLongID,
    RepeatedModifierID,
    InterpolatedStringErrorID,
    UnboundPlaceholderParameterID,
    IllegalStartSimpleExprID,
    MissingReturnTypeID,
    YieldOrDoExpectedInForComprehensionID,
    ProperDefinitionNotFoundID,
    ByNameParameterNotSupportedID,
    WrongNumberOfTypeArgsID,
    IllegalVariableInPatternAlternativeID,
    IdentifierExpectedID,
    AuxConstructorNeedsNonImplicitParameterID,
    IncorrectRepeatedParameterSyntaxID,
    IllegalLiteralID,
    PatternMatchExhaustivityID,
    MatchCaseUnreachableID,
    SeqWildcardPatternPosID,
    IllegalStartOfSimplePatternID,
    PkgDuplicateSymbolID,
    ExistentialTypesNoLongerSupportedID,
    UnboundWildcardTypeID,
    DanglingThisInPathID,
    OverridesNothingID,
    OverridesNothingButNameExistsID,
    ForwardReferenceExtendsOverDefinitionID,
    ExpectedTokenButFoundID,
    MixedLeftAndRightAssociativeOpsID,
    CantInstantiateAbstractClassOrTraitID,
    DUMMY_AVAILABLE_1,
    OverloadedOrRecursiveMethodNeedsResultTypeID,
    RecursiveValueNeedsResultTypeID,
    CyclicReferenceInvolvingID,
    CyclicReferenceInvolvingImplicitID,
    SuperQualMustBeParentID,
    AmbiguousImportID,
    MethodDoesNotTakeParametersId,
    AmbiguousOverloadID,
    ReassignmentToValID,
    TypeDoesNotTakeParametersID,
    ParameterizedTypeLacksArgumentsID,
    VarValParametersMayNotBeCallByNameID,
    MissingTypeParameterForID,
    DoesNotConformToBoundID,
    DoesNotConformToSelfTypeID,
    DoesNotConformToSelfTypeCantBeInstantiatedID,
    AbstractMemberMayNotHaveModifierID,
    TopLevelCantBeImplicitID,
    TypesAndTraitsCantBeImplicitID,
    OnlyClassesCanBeAbstractID,
    AbstractOverrideOnlyInTraitsID,
    TraitsMayNotBeFinalID,
    NativeMembersMayNotHaveImplementationID,
    OnlyClassesCanHaveDeclaredButUndefinedMembersID,
    CannotExtendAnyValID,
    CannotHaveSameNameAsID,
    ValueClassesMayNotDefineInnerID,
    ValueClassesMayNotDefineNonParameterFieldID,
    ValueClassesMayNotDefineASecondaryConstructorID,
    ValueClassesMayNotContainInitalizationID,
    ValueClassesMayNotBeAbstractID,
    ValueClassesMayNotBeContaintedID,
    ValueClassesMayNotWrapItselfID,
    ValueClassParameterMayNotBeAVarID,
    ValueClassNeedsExactlyOneValParamID,
    OnlyCaseClassOrCaseObjectAllowedID,
    ExpectedTopLevelDefID,
    AnonymousFunctionMissingParamTypeID,
    SuperCallsNotAllowedInlineableID,
    UNUSED1,  // not used anymore, but left so that error numbers stay the same
    WildcardOnTypeArgumentNotAllowedOnNewID,
    FunctionTypeNeedsNonEmptyParameterListID,
    WrongNumberOfParametersID,
    DuplicatePrivateProtectedQualifierID,
    ExpectedStartOfTopLevelDefinitionID,
    MissingReturnTypeWithReturnStatementID,
    NoReturnFromInlineableID,
    ReturnOutsideMethodDefinitionID,
    UncheckedTypePatternID,
    ExtendFinalClassID,
    EnumCaseDefinitionInNonEnumOwnerID,
    ExpectedTypeBoundOrEqualsID,
    ClassAndCompanionNameClashID,
    TailrecNotApplicableID,
    FailureToEliminateExistentialID,
    OnlyFunctionsCanBeFollowedByUnderscoreID,
    MissingEmptyArgumentListID,
    DuplicateNamedTypeParameterID,
    UndefinedNamedTypeParameterID,
    IllegalStartOfStatementID,
    TraitIsExpectedID,
    TraitRedefinedFinalMethodFromAnyRefID,
    PackageNameAlreadyDefinedID,
    UnapplyInvalidNumberOfArgumentsID,
    UnapplyInvalidReturnTypeID,
    StaticFieldsOnlyAllowedInObjectsID,
    CyclicInheritanceID,
    BadSymbolicReferenceID,
    UnableToExtendSealedClassID,
    SymbolHasUnparsableVersionNumberID,
    SymbolChangedSemanticsInVersionID,
    UnableToEmitSwitchID,
    MissingCompanionForStaticID,
    PolymorphicMethodMissingTypeInParentID,
    ParamsNoInlineID,
    JavaSymbolIsNotAValueID,
    DoubleDefinitionID,
    MatchCaseOnlyNullWarningID,
    ImportRenamedTwiceID,
    TypeTestAlwaysSucceedsID,
    TermMemberNeedsNeedsResultTypeForImplicitSearchID,
    ClassCannotExtendEnumID,
    ValueClassParameterMayNotBeCallByNameID,
    NotAnExtractorID,
    MemberWithSameNameAsStaticID,
    PureExpressionInStatementPositionID,
    TraitCompanionWithMutableStaticID,
    LazyStaticFieldID,
    StaticOverridingNonStaticMembersID,
    OverloadInRefinementID,
    NoMatchingOverloadID,
    StableIdentPatternID,
    StaticFieldsShouldPrecedeNonStaticID,
    IllegalSuperAccessorID,
    TraitParameterUsedAsParentPrefixID,
    UnknownNamedEnclosingClassOrObjectID,
    IllegalCyclicTypeReferenceID,
    MissingTypeParameterInTypeAppID,
    UNUSED_ImplicitTypesCanOnlyBeFunctionTypesID,
    ErasedTypesCanOnlyBeFunctionTypesID,
    CaseClassMissingNonImplicitParamListID,
    EnumerationsShouldNotBeEmptyID,
    AbstractCannotBeUsedForObjectsID,
    ModifierRedundantForObjectsID,
    TypedCaseDoesNotExplicitlyExtendTypedEnumID,
    IllegalRedefinitionOfStandardKindID,
    NoExtensionMethodAllowedID,
    ExtensionMethodCannotHaveTypeParamsID,
    ExtensionCanOnlyHaveDefsID,
    UnexpectedPatternForSummonFromID

  def errorNumber = ordinal - 2
}
