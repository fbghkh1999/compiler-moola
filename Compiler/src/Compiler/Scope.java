package Compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scope {
    private List<Feature> inner_level; // متغیر های داخلی
    private List<Scope> next_level;
    private Feature node;
    private ScopeKind kind;
    private Scope parent;
    private List<Assignment> assignments;


    public Scope(Feature node, ScopeKind kind, Scope parent) {
        this.node = node;
        this.kind = kind;
        this.next_level = new ArrayList<>();
        this.inner_level = new ArrayList<>();
        this.parent = parent;
        this.assignments = new ArrayList<>();
    }


    public Scope(Feature node, ScopeKind kind) {
        this(node, kind, null);
    }

    public List<Pair<Scope, Assignment>> scope2assignment() {
        List<Pair<Scope, Assignment>> list = new ArrayList<>();
        for (Assignment assignment : assignments) {
            list.add(new Pair<>(this, assignment));
        }
        for (Scope scope : next_level) {
            list.addAll(scope.scope2assignment());
        }

        return list;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public Scope getParent() {
        return parent;
    }

    public List<Feature> findSpecialType(ScopeType type) {
        List<Feature> featureList = new ArrayList<Feature>();
        for (Feature feature : inner_level) {
            if (feature.getType() == type) {
                featureList.add(feature);
            }
        }
        if (node != null && node.getType() == type) {
            featureList.add(node);
        }
        for (Scope scope : next_level) {
            featureList.addAll(scope.findSpecialType(type));
        }
        return featureList;
    }

    public List<Scope> find_all_scopes(ScopeKind type) {
        List<Scope> scopeList = new ArrayList<Scope>();
        if (this.kind == type) {
            scopeList.add(this);
        }
        for (Scope scope : next_level) {
            scopeList.addAll(scope.find_all_scopes(type));
        }
        return scopeList;
    }

    public Feature find_feature(String symbol, ScopeType type) {
        for (Feature feature : inner_level) {
            if (feature.getSymbol().equals(symbol) && feature.getType().equals(type)) {
                return feature;
            }
        }
        if (node != null && node.getType() == type) {
            return node;
        }
        for (Scope scope : next_level) {
            Feature feature = scope.find_feature(symbol, type);
            if (feature != null) {
                return feature;
            }
        }
        return null;
    }

    public Scope find_scope(String symbol, ScopeType type) {
        Scope answer = new Scope(new Feature(symbol, type, null, 0), null);
        for (Scope scope : next_level) {
            if (scope.equals(answer)) {
                return scope;
            }
            Scope child = scope.find_scope(symbol, type); // find in next level
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    public List<Feature> getInner_level() {
        return inner_level;
    }

    public List<Scope> getNext_level() {
        return next_level;
    }

    public Feature getNode() {
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scope scope = (Scope) o;
        return Objects.equals(node, scope.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }

    @Override
    public String toString() {
        // todo
        return "Scope{"
                + "inner_level="
                + inner_level
                + ", next_level="
                + next_level
                + ", node="
                + node
                + '}';
    }
}
