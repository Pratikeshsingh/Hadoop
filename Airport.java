package org.myorg;
import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class Airport extends Configured implements Tool
{
	public static void main(String[] args) throws Exception
	{
		int res = ToolRunner.run(new Airport(), args);
		System.exit(res);	
	}
	public int run(String[] args) throws Exception
	{
		Job job = Job.getInstance(getConf(), "airport");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job,new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path (args[1]));
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	public static class Map extends Mapper<LongWritable, Text, Text,IntWritable>
	{
		private static IntWritable one = new IntWritable(1);
		private Text key = new Text();
		String line, tokens[];
		public void map(LongWritable offset, Text linetext, Context context) throws IOException, InterruptedException
		{
			line = linetext.toString();
			tokens = line.split(",");
			if(tokens[21].equals("0"))
			{
				key.set(tokens[16]);
				context.write(key,one);
			}
			if (tokens[23].equals("0"))
			{
				key.set(tokens[17]);
				context.write(key,one);
			}
		}
	}
	public static class Reduce extends Reducer <Text, IntWritable, Text, IntWritable>
	{
		private IntWritable value = new IntWritable();
		int sum;
		public void reduce(Text key, Iterable<IntWritable> counts, Context context) throws IOException, InterruptedException 
		{
			sum = 0;
			for(IntWritable count:counts)
			{
				sum += count.get();
			}
			value.set(sum);
			context.write(key,value); 
		}
	}
}
