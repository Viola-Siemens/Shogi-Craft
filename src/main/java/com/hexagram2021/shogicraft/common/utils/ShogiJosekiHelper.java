package com.hexagram2021.shogicraft.common.utils;

import com.google.common.collect.Lists;
import com.hexagram2021.shogicraft.common.utils.ShogiGameEvaluator.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hexagram2021.shogicraft.common.entities.KomaEntity.Type.*;

public class ShogiJosekiHelper {
	// TODO: Use book.json for joseki input.
	private static final SingleLinkedGraph<Move> graph;

	static {
		//https://shogi-joutatsu.com/archives/category/sempou
		graph = new SingleLinkedGraph<>(new Move(false, new Pos(15, 15), new Pos(15, 15), OU));
		//矢倉
		graph.addSequence(
				new Move(true, new Pos(7, 7), new Pos(7, 6), FU),
				new Move(false, new Pos(8, 3), new Pos(8, 4), FU),
				new Move(true, new Pos(7, 9), new Pos(6, 8), GI),
				new Move(false, new Pos(3, 3), new Pos(3, 4), FU),
				new Move(true, new Pos(6, 8), new Pos(7, 7), GI)
		);
		//角換り
		SingleLinkedGraph.addChildToAll(
				graph.addSequence(
						new Move(true, new Pos(7, 7), new Pos(7, 6), FU),
						new Move(false, new Pos(8, 3), new Pos(8, 4), FU),
						new Move(true, new Pos(2, 7), new Pos(2, 6), FU),
						new Move(false, new Pos(8, 4), new Pos(8, 5), FU),
						new Move(true, new Pos(8, 8), new Pos(7, 7), KA),
						new Move(false, new Pos(3, 3), new Pos(3, 4), FU)
				).addChildren(
						new Move(true, new Pos(7, 9), new Pos(8, 8), GI),
						new Move(true, new Pos(7, 9), new Pos(6, 8), GI)
				).stream().map(gn -> gn.addChild(
						new Move(false, new Pos(2, 2), new Pos(7, 7), UM)
				)).collect(Collectors.toList()),
				new Move(true, new Pos(2, 2), new Pos(7, 7), GI)
		);
		//相掛かり
		SingleLinkedGraph.GraphNode<Move> aigakari = graph.addSequence(
				new Move(true, new Pos(2, 7), new Pos(2, 6), FU),
				new Move(false, new Pos(8, 3), new Pos(8, 4), FU),
				new Move(true, new Pos(2, 6), new Pos(2, 5), FU),
				new Move(false, new Pos(8, 4), new Pos(8, 5), FU)
		);
		aigakari.addSequence(
				new Move(true, new Pos(6, 9), new Pos(7, 8), KI),
				new Move(false, new Pos(4, 1), new Pos(3, 2), KI),
				new Move(true, new Pos(2, 5), new Pos(2, 4), FU),
				new Move(false, new Pos(2, 3), new Pos(2, 4), FU),
				new Move(true, new Pos(2, 8), new Pos(2, 4), HI)
		);
		aigakari.addSequence(
				new Move(true, new Pos(7, 7), new Pos(7, 6), FU),
				new Move(false, new Pos(4, 1), new Pos(3, 2), KI),
				new Move(true, new Pos(8, 8), new Pos(7, 7), KA)
		);
		//対振り飛車
		SingleLinkedGraph.GraphNode<Move> taifuri = graph.addSequence(
				new Move(true, new Pos(7, 7), new Pos(7, 6), FU),
				new Move(false, new Pos(3, 3), new Pos(3, 4), FU),
				new Move(true, new Pos(2, 7), new Pos(2, 6), FU),
				new Move(false, new Pos(4, 3), new Pos(4, 4), FU),
				new Move(true, new Pos(3, 9), new Pos(4, 8), GI),
				new Move(false, new Pos(8, 2), new Pos(4, 2), HI),
				new Move(true, new Pos(5, 9), new Pos(6, 8), OU)
		);
		List<SingleLinkedGraph.GraphNode<Move>> taifuri_list = Lists.newArrayList(
				taifuri.addSequence(
						new Move(false, new Pos(2, 2), new Pos(3, 3), KA),
						new Move(true, new Pos(6, 8), new Pos(7, 8), OU),
						new Move(false, new Pos(3, 1), new Pos(3, 2), GI)
				),
				taifuri.addSequence(
						new Move(false, new Pos(3, 1), new Pos(3, 2), GI),
						new Move(true, new Pos(6, 8), new Pos(7, 8), OU),
						new Move(false, new Pos(2, 2), new Pos(3, 3), KA)
				)
		);
		SingleLinkedGraph.addChildToAll(
				taifuri_list,
				new Move(true, new Pos(4, 9), new Pos(5, 8), KI)
		);
		SingleLinkedGraph.addChildToAll(
				taifuri_list,
				new Move(true, new Pos(5, 7), new Pos(5, 6), FU)
		).addSequence(
				new Move(false, new Pos(5, 1), new Pos(6, 2), OU),
				new Move(true, new Pos(4, 9), new Pos(5, 8), KI)
		);
	}

	@SuppressWarnings("UnusedReturnValue")
	public static class SingleLinkedGraph<T> {
		private final GraphNode<T> root;
		private final T illegal;

		public SingleLinkedGraph(T illegal) {
			this.root = new GraphNode<>(illegal);
			this.illegal = illegal;
		}

		public static class GraphNode<T> {
			private final List<GraphNode<T>> children = Lists.newArrayList();
			private final T value;

			public GraphNode(T value) {
				this.value = value;
			}

			public T getValue() {
				return this.value;
			}

			public boolean is(T value) {
				return this.value.equals(value);
			}

			public GraphNode<T> addChild(T value) {
				return SingleLinkedGraph.addChild(this, value);
			}

			@SafeVarargs
			public final List<GraphNode<T>> addChildren(T... values) {
				return SingleLinkedGraph.addChildren(this, values);
			}

			@SafeVarargs
			public final GraphNode<T> addSequence(T... values) {
				return SingleLinkedGraph.addSequenceFrom(this, values);
			}
		}

		public static <T> GraphNode<T> addChild(GraphNode<T> pos, T value) {
			Optional<GraphNode<T>> optional = pos.children.stream().filter(tn -> tn.is(value)).findAny();
			if(optional.isPresent()) {
				return optional.get();
			}
			GraphNode<T> ret = new GraphNode<>(value);
			pos.children.add(ret);
			return ret;
		}

		@SafeVarargs
		public static <T> List<GraphNode<T>> addChildren(GraphNode<T> pos, T... values) {
			List<GraphNode<T>> ret = Lists.newArrayList();
			for(T value: values) {
				ret.add(addChild(pos, value));
			}
			return ret;
		}

		public static <T> List<GraphNode<T>> addChildren(GraphNode<T> pos, Iterable<T> seq) {
			List<GraphNode<T>> ret = Lists.newArrayList();
			for(T value: seq) {
				ret.add(addChild(pos, value));
			}
			return ret;
		}

		public static <T> GraphNode<T> addChildToAll(List<GraphNode<T>> parents, T value) {
			GraphNode<T> ret = null;
			for(GraphNode<T> parent: parents) {
				Optional<GraphNode<T>> optional = parent.children.stream().filter(tn -> tn.is(value)).findAny();
				if(optional.isPresent()) {
					if(ret == null) {
						ret = optional.get();
					} else if(ret != optional.get()) {
						throw new IllegalStateException(String.format("Multiple nodes have already have child \"%s\"", value));
					}
				}
			}
			if(ret == null) {
				ret = new GraphNode<T>(value);
			}
			for(GraphNode<T> parent: parents) {
				parent.children.add(ret);
			}
			return ret;
		}

		@SafeVarargs
		public static <T> GraphNode<T> addSequenceFrom(GraphNode<T> pos, T... values) {
			GraphNode<T> hot = pos;
			for(T value: values) {
				hot = addChild(hot, value);
			}
			return hot;
		}

		public static <T> GraphNode<T> addSequenceFrom(GraphNode<T> pos, Iterable<T> seq) {
			GraphNode<T> hot = pos;
			for(T value: seq) {
				hot = addChild(hot, value);
			}
			return hot;
		}

		@SafeVarargs
		public final GraphNode<T> addSequence(T... values) {
			return addSequenceFrom(this.root, values);
		}

		public GraphNode<T> addSequence(Iterable<T> seq) {
			return addSequenceFrom(this.root, seq);
		}
	}
}
