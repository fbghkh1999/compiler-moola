package Compiler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ErrorFinder {
    private Scope root;

    public ErrorFinder(Scope root) {
        this.root = root;
    }

    public boolean running() {
        boolean error = ERROR101();
        error = error | ERROR102();
        error = error | ERROR103();
        error = error | ERROR104();
        error = error | ERROR105();
        error = error | Error410();
        return error;
    }

    public boolean ERROR101() {
        List<Feature> className = root.findSpecialType(ScopeType.CLASS);
        Collections.sort(className);
        return havereapited(className, "ERROR 101 , line %d , name %s has been defined already");
    }

    private boolean havereapited(List<Feature> featurelist, String formatter) {
        for (int i = 0; i < featurelist.size() - 1; i++) {
            if (featurelist.get(i).getSymbol().equals(featurelist.get(i + 1).getSymbol())) {
                System.out.println(String.format(formatter, featurelist.get(i + 1).getLineNumber(), featurelist.get(i + 1).getSymbol()));
                return true;
            }
        }
        return false;
    }

    public boolean ERROR102() {
        List<Scope> classScopeList = root.find_all_scopes(ScopeKind.CLASS);
        for (Scope scope : classScopeList) {
            List<Feature> featureList = scope.getNext_level().stream().map(scope1 -> scope1.getNode()).filter(feature -> feature.getType() == ScopeType.METHOD).sorted().collect(Collectors.toList());
            if (havereapited(featureList, "ERROR 102 , line %d , name %s  has been defined already")) {
                return true;
            }
        }
        return false;
    }

    public Feature inParentOrSelf(Scope child, String symbol, ScopeType type) {
        if (child == null) {
            return null;
        }
        for (Feature feature : child.getInner_level()) {
            if (feature.getSymbol().equals(symbol) && feature.getType().equals(type)) {
                return feature;
            }
        }
        return inParentOrSelf(child.getParent(), symbol, type);
    }

    public boolean ERROR103() { //  تعریف دوباره یک مرییر میله در یک حوزه todo
        List<Scope> classScopeList = root.find_all_scopes(ScopeKind.METHOD);
        for (Scope scope : classScopeList) {
            List<Feature> featureList = scope.getNext_level().stream().map(scope1 -> scope1.getNode()).filter(feature -> feature.getType() == ScopeType.BLOCK).sorted().collect(Collectors.toList());
            if (havereapited(featureList, "ERROR 103 , line %d , name %s  has been defined already")) {
                return true;
            }
        }
        return false;
    }

    public boolean ERROR105() {
        List<Pair<Scope, Assignment>> scope2assignment = root.scope2assignment();
        for (Pair<Scope, Assignment> assignmentPair : scope2assignment) {
            String[] rightVal = assignmentPair.getSecond().getValue().trim().split(" ");
            for (int i = 0; i < rightVal.length; i++) {
                if (rightVal[i].trim().equals("new")) {
                    String className = rightVal[i + 1];
                    if (inParentOrSelf(assignmentPair.getFirst(), className, ScopeType.CLASS) == null) {
                        System.out.println("ERROR105 cannot find class " + assignmentPair.getSecond().toString()+assignmentPair.getSecond().getLineNumber());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean ERROR104() {
        List<Scope> classScopeList = root.find_all_scopes(ScopeKind.CLASS);
        for (Scope scope : classScopeList) {
            List<Feature> featureList = scope.getInner_level().stream().filter(feature -> feature.getType() == ScopeType.VAR).sorted().collect(Collectors.toList());
            if (havereapited(featureList, "ERROR 104 , line %d , name %s  has been defined already")) {
                return true;
            }
        }
        return false;
    }


    public boolean Error410() {
        List<Feature> className = root.findSpecialType(ScopeType.CLASS);
        for (Feature feature : className) {
            Set<Feature> parents = new HashSet<>();
            Feature current = feature;
            parents.add(current);
            while (current != null) {
                if (parents.contains(current.getClassParent())) {
                    System.out.println("Error410 invalid inheritance line"+current.getLineNumber()+"class1"+current.getSymbol()+"class2"+"");
                    return true;
                }
                parents.add(current.getClassParent());
                current = current.getClassParent();
            }
        }
        return false;
    }
}
